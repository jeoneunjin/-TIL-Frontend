# [React] WebRTC + WebSocket 기반 실시간 협업 촬영 플로우 설계

> **Date:** 2026-01-30  
> **Tag:** #Frontend #TIL #WebRTC #WebSocket

---

## 📋 목차

1. [전체 데이터 흐름 및 동기화 메커니즘 설명](#1-전체-데이터-흐름-및-동기화-매커니즘-설명)
2. [핵심 데이터 흐름 패턴](#2-핵심-데이터-흐름-패턴)
3. [동기화 목록](#3-동기화-목록)

---

## 1. 전체 데이터 흐름 및 동기화 메커니즘 설명

### 1-1. 전체 아키텍처 흐름도

```
┌─────────────────────────────────────────────────────────────────┐
│                         Browser A (User 1)                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐      │
│  │  Component   │───→│  Zustand     │───→│  WebSocket   │      │
│  │  (UI Layer)  │←───│  Store       │←───│  Hook        │      │
│  └──────────────┘    └──────────────┘    └──────────────┘      │
│         │                    │                    │              │
│         │ 1. User Action     │ 2. Update State    │ 3. Send WS   │
│         ↓                    ↓                    ↓              │
└─────────────────────────────────────────────────────────────────┘
                                                     │
                                                     │ WebSocket
                                                     ↓
┌─────────────────────────────────────────────────────────────────┐
│                      WebSocket Server                            │
│                    (Signaling Server)                            │
│                                                                   │
│  - 방(Room) 관리                                                  │
│  - 메시지 브로드캐스트                                             │
│  - WebRTC Signaling 중계                                          │
└─────────────────────────────────────────────────────────────────┘
                                                     │
                                                     │ Broadcast
                                                     ↓
┌─────────────────────────────────────────────────────────────────┐
│                         Browser B (User 2)                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐      │
│  │  Component   │←───│  Zustand     │←───│  WebSocket   │      │
│  │  (UI Layer)  │───→│  Store       │───→│  Hook        │      │
│  └──────────────┘    └──────────────┘    └──────────────┘      │
│         ↑                    ↑                    ↑              │
│         │ 4. UI Update       │ 5. Receive & Parse │ 6. WS Event  │
│         │                    │                    │              │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. 핵심 데이터 흐름 패턴

### 패턴 A: 로컬 액션 → 전역 동기화

```tsx
// 1. 사용자 액션
Button Click
  ↓
// 2. 로컬 상태 즉시 업데이트 (Optimistic Update)
Local State Update (useState or Zustand)
  ↓
// 3. 다른 유저들에게 알림
WebSocket.send({ type: 'EVENT', payload: data })
  ↓
// 4. Server 브로드캐스트
Server → All Clients
  ↓
// 5. 다른 클라이언트들이 수신 → Store 업데이트
Other Clients: Store Update → UI Re-render
```

### 패턴 B: 수신 메시지 → UI 반영

```tsx
// 1. WebSocket 메시지 수신
ws.onmessage = (event) => { ... }
  ↓
// 2. 메시지 파싱
const message = JSON.parse(event.data)
  ↓
// 3. 이벤트 타입별 핸들러 찾기
const handlers = messageHandlers.get(message.type)
  ↓
// 4. 핸들러 실행 (Store 업데이트)
handlers.forEach(handler => handler(message.payload))
  ↓
// 5. Zustand가 구독자들에게 알림
Zustand notifies subscribers
  ↓
// 6. React Component 리렌더링
Component Re-render with new data
```

---

## 3. 동기화 목록

### 목록

| **화면**  | **동기화 내용** |
| --- | --- |
| 공통 | 다음으로 넘어가기 |
| 대기실 | 준비 상태 |
| 배경 선택 | 단색 선택 |
|  | 사진 업로드 |
| 촬영 | 촬영 시작(시작 후 카운트 다운) |
|  | 다시 찍기(다시 찍기 상태 동기화 → 촬영 다시 시작) |
| 사진 꾸미기 | 아직 미정 |

### 대기실-준비 상태 동기화

```markdown
[User 1 Browser - 대기실]

1. 사용자가 "준비하기" 버튼 클릭
   ↓
2. Component에서 toggleReady() 호출
   
   // WaitingRoom.tsx
   const handleReadyClick = () => {
     toggleReady(); // Hook 호출
   }
   
   ↓
3. useReadyStatus Hook이 실행
   
   const toggleReady = () => {
     const newReadyState = !isReady;
     setIsReady(newReadyState);  // ← 로컬 state 업데이트
     
     // Store 업데이트 (User 1의 로컬 상태)
     updateStageState(localUserId, { isReady: true });
     
     // WebSocket으로 다른 유저들에게 전송
     send(WSEventType.READY_STATUS, {
       userId: 'user1',
       stage: 'waiting-room',
       isReady: true
     });
   }
   
   ↓
4. Zustand Store 업데이트 (User 1 로컬)
   
   // useParticipantsStore.ts
   participants: Map {
     'user1' => { 
       userId: 'user1',
       stageState: { isReady: true }  // ← 업데이트됨
     },
     'user2' => { 
       userId: 'user2',
       stageState: { isReady: false }
     }
   }
   
   ↓
5. WebSocket으로 메시지 전송
   
   // useConnectionStore.ts
   ws.send(JSON.stringify({
     type: 'READY_STATUS',
     payload: { userId: 'user1', isReady: true },
     from: 'user1',
     roomId: 'room123',
     timestamp: 1234567890
   }));
   
   ↓
6. Signaling Server가 메시지 수신
   ↓
7. Server가 같은 방의 모든 유저에게 브로드캐스트
   (User 1, User 2, User 3 등...)

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

[User 2 Browser - 대기실]

8. User 2의 WebSocket이 메시지 수신
   
   ws.onmessage = (event) => {
     const message = JSON.parse(event.data);
     // message = {
     //   type: 'READY_STATUS',
     //   payload: { userId: 'user1', isReady: true },
     //   from: 'user1'
     // }
   }
   
   ↓
9. 등록된 핸들러 실행
   
   // useConnectionStore.ts
   const handlers = messageHandlers.get('READY_STATUS');
   handlers.forEach(handler => handler(message.payload, message.from));
   
   ↓
10. useStageSync에 등록된 핸들러 실행
   
   // WaitingRoom.tsx (User 2)
   useStageSync({
     eventHandlers: {
       [WSEventType.READY_STATUS]: (payload, from) => {
         // payload = { userId: 'user1', isReady: true }
         // from = 'user1'
         
         setUserReady(from, payload.isReady);
         updateStageState(from, { isReady: payload.isReady });
       }
     }
   })
   
   ↓
11. User 2의 Zustand Store 업데이트
   
   participants: Map {
     'user1' => { 
       userId: 'user1',
       stageState: { isReady: true }  // ← User 2도 업데이트됨!
     },
     'user2' => { 
       userId: 'user2',
       stageState: { isReady: false }
     }
   }
   
   ↓
12. React가 상태 변화 감지 → 자동 리렌더링
   ↓
13. User 2의 화면에 User 1이 "준비 완료" 상태로 표시됨!
```

### 배경-단색 선택

```markdown
┌─────────────────────────────────────────────────────────────────┐
│ User 1: 파란색 배경 클릭                                          │
└─────────────────────────────────────────────────────────────────┘

[User 1 Browser - 배경 선택 화면]
  │
  │ 1. 사용자가 "파란색" 배경 버튼 클릭
  ↓
┌──────────────────────────────────────────────────────────────────┐
│ SelectBg Component (User 1)                                       │
│                                                                   │
│ const handleSelectBg = (bgId: string, bgType: 'color') => {     │
│   setSelectedBg(bgId);  // 로컬 UI state: 'blue'                │
│   selectBackground(localUserId, bgId, bgType);  // Store 업데이트 │
│   send(WSEventType.BG_SELECTED, {                                │
│     bgId: 'blue',                                                 │
│     bgType: 'color',                                              │
│     bgData: '#3B82F6'  // 실제 색상 값                            │
│   });                                                             │
│ }                                                                 │
└──────────────────────────────────────────────────────────────────┘
  │
  │ 2. Zustand Store 업데이트 (User 1 로컬)
  ↓
┌──────────────────────────────────────────────────────────────────┐
│ useBgSelectStore (User 1)                                         │
│                                                                   │
│ stageData: {                                                      │
│   availableBackgrounds: [                                         │
│     { id: 'white', type: 'color', data: '#FFFFFF' },            │
│     { id: 'blue', type: 'color', data: '#3B82F6' },             │
│     { id: 'pink', type: 'color', data: '#EC4899' }              │
│   ],                                                              │
│   userSelections: Map {                                           │
│     'user1' => {                                                  │
│       bgId: 'blue',                                               │
│       bgType: 'color',                                            │
│       bgData: '#3B82F6'  ← User 1의 선택 저장                     │
│     }                                                             │
│   }                                                               │
│ }                                                                 │
└──────────────────────────────────────────────────────────────────┘
  │
  │ 3. WebSocket 메시지 전송
  ↓
┌──────────────────────────────────────────────────────────────────┐
│ WebSocket Message                                                 │
│                                                                   │
│ {                                                                 │
│   type: 'BG_SELECTED',                                            │
│   payload: {                                                      │
│     bgId: 'blue',                                                 │
│     bgType: 'color',                                              │
│     bgData: '#3B82F6'                                             │
│   },                                                              │
│   from: 'user1',                                                  │
│   roomId: 'room123',                                              │
│   timestamp: 1738234567890                                        │
│ }                                                                 │
└──────────────────────────────────────────────────────────────────┘
  │
  │ 4. Server가 room123의 모든 유저에게 브로드캐스트
  ↓
┌──────────────────────────────────────────────────────────────────┐
│ User 2, User 3에게 전송                                           │
└──────────────────────────────────────────────────────────────────┘

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

[User 2 Browser - 배경 선택 화면]
  │
  │ 5. User 2의 WebSocket이 메시지 수신
  ↓
┌──────────────────────────────────────────────────────────────────┐
│ useConnectionStore (User 2)                                       │
│                                                                   │
│ ws.onmessage = (event) => {                                      │
│   const message = JSON.parse(event.data);                        │
│   // message.type = 'BG_SELECTED'                                │
│   // message.payload = { bgId: 'blue', bgType: 'color', ... }   │
│   // message.from = 'user1'                                      │
│                                                                   │
│   // 등록된 핸들러 찾기                                           │
│   const handlers = messageHandlers.get('BG_SELECTED');           │
│   handlers.forEach(handler => handler(message.payload, 'user1'))│
│ }                                                                 │
└──────────────────────────────────────────────────────────────────┘
  │
  │ 6. useStageSync에 등록된 핸들러 실행
  ↓
┌──────────────────────────────────────────────────────────────────┐
│ SelectBg Component (User 2)                                       │
│                                                                   │
│ useStageSync({                                                    │
│   stage: 'select-bg',                                             │
│   eventHandlers: {                                                │
│     [WSEventType.BG_SELECTED]: (payload, from) => {              │
│       // payload = {                                             │
│       //   bgId: 'blue',                                         │
│       //   bgType: 'color',                                      │
│       //   bgData: '#3B82F6'                                     │
│       // }                                                       │
│       // from = 'user1'                                          │
│                                                                   │
│       selectBackground(from, payload.bgId, payload.bgType,       │
│                       payload.bgData);                           │
│     }                                                             │
│   }                                                               │
│ });                                                               │
└──────────────────────────────────────────────────────────────────┘
  │
  │ 7. User 2의 Store 업데이트
  ↓
┌──────────────────────────────────────────────────────────────────┐
│ useBgSelectStore (User 2)                                         │
│                                                                   │
│ stageData: {                                                      │
│   userSelections: Map {                                           │
│     'user1' => {                                                  │
│       bgId: 'blue',                                               │
│       bgType: 'color',                                            │
│       bgData: '#3B82F6'  ← User 2의 Store에도 반영!              │
│     }                                                             │
│   }                                                               │
│ }                                                                 │
└──────────────────────────────────────────────────────────────────┘
  │
  │ 8. React 리렌더링
  ↓
┌──────────────────────────────────────────────────────────────────┐
│ User 2의 화면                                                      │
│                                                                   │
│ ┌────────────────────────┐                                       │
│ │ 배경 선택 (2/3명 선택 완료) │                                    │
│ ├────────────────────────┤                                       │
│ │                        │                                       │
│ │ 참가자 선택 현황:        │                                       │
│ │ • User 1: ✓ 파란색 배경  │  ← 업데이트됨!                       │
│ │ • User 2: 선택 중...     │                                       │
│ │ • User 3: 선택 중...     │                                       │
│ │                        │                                       │
│ │ [투표 현황]             │                                       │
│ │ 파란색: 1표 ⬤           │  ← 파란색 동그라미 표시               │
│ │ 흰색: 0표               │                                       │
│ │ 핑크색: 0표             │                                       │
│ └────────────────────────┘                                       │
└──────────────────────────────────────────────────────────────────┘
```

### 배경-사진 업로드

```markdown
┌─────────────────────────────────────────────────────────────────┐
│ User 1: 자신의 PC에서 이미지 파일 선택                            │
└─────────────────────────────────────────────────────────────────┘

[User 1 Browser - 배경 선택 화면]
  │
  │ 1. 파일 선택 (예: my-background.jpg)
  ↓
┌──────────────────────────────────────────────────────────────────┐
│ SelectBg Component (User 1)                                       │
│                                                                   │
│ const handleFileUpload = async (e: ChangeEvent<HTMLInputElement>) => { │
│   const file = e.target.files?.[0];                              │
│   if (!file) return;                                              │
│                                                                   │
│   // 1-1. 파일을 Base64로 변환 (작은 파일)                        │
│   const reader = new FileReader();                                │
│   reader.onload = async (e) => {                                 │
│     const base64Data = e.target?.result as string;               │
│     // "data:image/jpeg;base64,/9j/4AAQSkZJRg..."               │
│                                                                   │
│     // 또는 큰 파일의 경우 서버에 업로드                          │
│     // const uploadedUrl = await uploadToServer(file);           │
│                                                                   │
│     const bgId = `custom-${Date.now()}`;                         │
│     handleSelectBg(bgId, 'upload', base64Data);                  │
│   };                                                              │
│   reader.readAsDataURL(file);                                    │
│ }                                                                 │
│                                                                   │
│ const handleSelectBg = (bgId, bgType, bgData) => {              │
│   setSelectedBg(bgId);                                           │
│   selectBackground(localUserId, bgId, bgType, bgData);           │
│   send(WSEventType.BG_SELECTED, { bgId, bgType, bgData });      │
│ }                                                                 │
└──────────────────────────────────────────────────────────────────┘
  │
  │ 2. Store 업데이트 (User 1 로컬)
  ↓
┌──────────────────────────────────────────────────────────────────┐
│ useBgSelectStore (User 1)                                         │
│                                                                   │
│ stageData: {                                                      │
│   availableBackgrounds: [                                         │
│     { id: 'blue', type: 'color', data: '#3B82F6' },             │
│     {                                                             │
│       id: 'custom-1738234567890',                                │
│       type: 'upload',                                             │
│       data: 'data:image/jpeg;base64,/9j/4AAQSkZJRg...',         │
│       uploadedBy: 'user1'  ← 누가 업로드했는지                   │
│     }                                                             │
│   ],                                                              │
│   userSelections: Map {                                           │
│     'user1' => {                                                  │
│       bgId: 'custom-1738234567890',                              │
│       bgType: 'upload',                                           │
│       bgData: 'data:image/jpeg;base64,/9j/4AAQSkZJRg...'        │
│     }                                                             │
│   }                                                               │
│ }                                                                 │
└──────────────────────────────────────────────────────────────────┘
  │
  │ 3. WebSocket으로 Base64 데이터 전송
  ↓
┌──────────────────────────────────────────────────────────────────┐
│ WebSocket Message (큰 메시지!)                                    │
│                                                                   │
│ {                                                                 │
│   type: 'BG_SELECTED',                                            │
│   payload: {                                                      │
│     bgId: 'custom-1738234567890',                                │
│     bgType: 'upload',                                             │
│     bgData: 'data:image/jpeg;base64,/9j/4AAQSkZJRg...',  ← 큰 데이터! │
│     uploadedBy: 'user1',                                          │
│     fileName: 'my-background.jpg'                                │
│   },                                                              │
│   from: 'user1',                                                  │
│   roomId: 'room123',                                              │
│   timestamp: 1738234567890                                        │
│ }                                                                 │
│                                                                   │
│ ⚠️ 주의: Base64는 원본 파일보다 ~33% 더 큼                         │
│ 100KB 이미지 → ~133KB Base64 문자열                               │
└──────────────────────────────────────────────────────────────────┘
  │
  │ 4. Server 브로드캐스트
  ↓

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

[User 2 Browser]
  │
  │ 5. User 2가 메시지 수신
  ↓
┌──────────────────────────────────────────────────────────────────┐
│ useStageSync (User 2)                                             │
│                                                                   │
│ eventHandlers: {                                                  │
│   [WSEventType.BG_SELECTED]: (payload, from) => {                │
│     // payload = {                                               │
│     //   bgId: 'custom-1738234567890',                          │
│     //   bgType: 'upload',                                       │
│     //   bgData: 'data:image/jpeg;base64,...'  ← Base64 받음     │
│     // }                                                         │
│                                                                   │
│     if (payload.bgType === 'upload') {                           │
│       // 새로운 배경 옵션으로 추가                                │
│       addAvailableBackground({                                   │
│         id: payload.bgId,                                        │
│         type: 'upload',                                           │
│         data: payload.bgData,                                    │
│         uploadedBy: from                                         │
│       });                                                         │
│     }                                                             │
│                                                                   │
│     selectBackground(from, payload.bgId, payload.bgType,         │
│                     payload.bgData);                             │
│   }                                                               │
│ }                                                                 │
└──────────────────────────────────────────────────────────────────┘
  │
  │ 6. User 2의 Store 업데이트
  ↓
┌──────────────────────────────────────────────────────────────────┐
│ useBgSelectStore (User 2)                                         │
│                                                                   │
│ stageData: {                                                      │
│   availableBackgrounds: [                                         │
│     { id: 'blue', type: 'color', data: '#3B82F6' },             │
│     {                                                             │
│       id: 'custom-1738234567890',  ← 새로 추가됨!                │
│       type: 'upload',                                             │
│       data: 'data:image/jpeg;base64,/9j/4AAQSkZJRg...',         │
│       uploadedBy: 'user1'                                         │
│     }                                                             │
│   ],                                                              │
│   userSelections: Map {                                           │
│     'user1' => {                                                  │
│       bgId: 'custom-1738234567890',                              │
│       bgType: 'upload',                                           │
│       bgData: 'data:image/jpeg;base64,...'                       │
│     }                                                             │
│   }                                                               │
│ }                                                                 │
└──────────────────────────────────────────────────────────────────┘
  │
  │ 7. UI 업데이트
  ↓
┌──────────────────────────────────────────────────────────────────┐
│ User 2의 화면                                                      │
│                                                                   │
│ ┌────────────────────────────────────────────────────┐           │
│ │ 배경 선택                                            │           │
│ ├────────────────────────────────────────────────────┤           │
│ │                                                    │           │
│ │ [기본 배경]                                         │           │
│ │ ⬜ 흰색  🔵 파란색  🌸 핑크색                        │           │
│ │                                                    │           │
│ │ [업로드된 배경]                                     │           │
│ │ ┌─────────┐                                        │           │
│ │ │ [이미지] │  ← User 1이 업로드한 배경이 나타남!     │           │
│ │ │  미리보기│     (Base64로 받아서 <img> 표시)       │           │
│ │ │ by User1│                                        │           │
│ │ └─────────┘                                        │           │
│ │                                                    │           │
│ │ 참가자 선택 현황:                                   │           │
│ │ • User 1: ✓ 업로드 배경 (my-background.jpg)        │           │
│ │ • User 2: 선택 중...                                │           │
│ └────────────────────────────────────────────────────┘           │
└──────────────────────────────────────────────────────────────────┘
```

### 촬영-촬영 시작

```markdown
┌─────────────────────────────────────────────────────────────────┐
│ Step 1: 호스트(User 1)가 "촬영 시작" 버튼 클릭                   │
└─────────────────────────────────────────────────────────────────┘

[User 1 - Host]
  │
  │ handleStartShoot() 실행
  ↓
  send(WSEventType.COUNTDOWN_START, {})
  │
  │ WebSocket 메시지 전송
  │ { type: 'COUNTDOWN_START', from: 'user1', roomId: 'room123' }
  ↓
[Server]
  │
  │ room123의 모든 유저에게 브로드캐스트
  ↓
[User 1, User 2, User 3 모두 동시에 수신]

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

┌─────────────────────────────────────────────────────────────────┐
│ Step 2: 모든 유저가 카운트다운 시작                               │
└─────────────────────────────────────────────────────────────────┘

[User 1]                [User 2]                [User 3]
    │                       │                       │
    │ 메시지 수신             │ 메시지 수신            │ 메시지 수신
    ↓                       ↓                       ↓
useStageSync 핸들러      useStageSync 핸들러     useStageSync 핸들러
    │                       │                       │
    ↓                       ↓                       ↓
startCountdown()        startCountdown()        startCountdown()
    │                       │                       │
    ↓                       ↓                       ↓
runCountdown() 실행     runCountdown() 실행     runCountdown() 실행
    │                       │                       │
    ↓                       ↓                       ↓
Store 업데이트:         Store 업데이트:          Store 업데이트:
countdown: 3            countdown: 3             countdown: 3
    │                       │                       │
    ↓                       ↓                       ↓
UI에 "3" 표시            UI에 "3" 표시            UI에 "3" 표시

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1초 후...

[User 1]                [User 2]                [User 3]
    │                       │                       │
    ↓                       ↓                       ↓
updateCountdown(2)      updateCountdown(2)      updateCountdown(2)
    │                       │                       │
    ↓                       ↓                       ↓
UI에 "2" 표시            UI에 "2" 표시            UI에 "2" 표시

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1초 후...

[User 1]                [User 2]                [User 3]
    │                       │                       │
    ↓                       ↓                       ↓
updateCountdown(1)      updateCountdown(1)      updateCountdown(1)
    │                       │                       │
    ↓                       ↓                       ↓
UI에 "1" 표시            UI에 "1" 표시            UI에 "1" 표시

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1초 후...

[User 1]                [User 2]                [User 3]
    │                       │                       │
    ↓                       ↓                       ↓
handleCapture()         handleCapture()         handleCapture()
    │                       │                       │
    ↓                       ↓                       ↓
캔버스에 사진 합성       캔버스에 사진 합성       캔버스에 사진 합성
    │                       │                       │
    ↓                       ↓                       ↓
capturePhoto(data)      capturePhoto(data)      capturePhoto(data)
    │                       │                       │
    ↓                       ↓                       ↓
send(PHOTO_CAPTURE)     send(PHOTO_CAPTURE)     send(PHOTO_CAPTURE)

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

결과: 모든 유저가 정확히 같은 타이밍에 촬영!
```
