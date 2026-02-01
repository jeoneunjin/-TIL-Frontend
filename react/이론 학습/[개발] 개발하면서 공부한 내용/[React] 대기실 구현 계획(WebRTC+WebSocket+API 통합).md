# [React] 대기실 WebRTC+WebSocket+API 통합 설계 전략

> **Created:** 2026-01-31  
> **Last Updated:** 2026-02-01  
> **Tag:** #Frontend #WebRTC #WebSocket #API #TIL

---

## 📋 목차
1. [최신 전체 시나리오](#1-최신-전체-시나리오)
   - [방장 시나리오](#1-1-방장-시나리오)
   - [게스트 시나리오](#1-2-게스트-시나리오)
   - [기타 공통 사항](#1-3-기타-공통-사항)
2. [전체 구현 로드맵](#2-전체-구현-로드맵)
   - [Phase 1: Store 구조 설계 및 확장](#2-1-Phase-1-Store-구조-설계-및-확장)
   - [Phase 2: API 통합 및 방 정보 관리](#2-2-Phase-2-API-통합)
   - [Phase 3: WebSocket 이벤트 핸들러 구현](#2-3-Phase-3-WebSocket-이벤트-핸들러-구현)
   - [Phase 4: UI 컴포넌트 개발](#2-4-Phase-4-UI-컴포넌트-개발)
3. [Notification(알림) & Auth(인증) 관련 구조 정리](#3-notification알림--auth인증-관련-구조-정리)

---

## 1. 최신 전체 시나리오

### 1-1. 방장 시나리오

1. **방 생성 및 입장**
   - 아바타 선택 후 “다음으로 넘어가기” 클릭 시 방 생성 API 호출 → 방 코드(roomCode) 발급.
   - 방장이 자동으로 해당 방에 join(입장)됨.
   - 마이크/카메라는 기본적으로 켜진 상태(개별 on/off 가능).

2. **친구 초대**
   - 초대는 “이름 검색”을 통해서만 가능(방 링크 공유 없음).
   - 이름 검색 → 선택 → 초대 보내기(초대 API 호출).
   - 초대받은 사용자는 사이트 내 알림(Notification)으로 초대 수락 가능.
   - 인원 수가 꽉 찬 방에는 초대 불가(백엔드 예외 처리).

3. **대기실(Waiting Room)**
   - 참가자 목록, 각자의 마이크/카메라 상태, 레디(Ready) 상태가 실시간 동기화되어 표시.
   - 방장은 초대 버튼이 보이고, 게스트는 초대 버튼이 보이지 않음.
   - 인원 수가 꽉 찬 경우, 추가 입장자는 “인원이 꽉 찼습니다” 모달만 보고 홈으로 돌아감(카메라/마이크 UI 없음).

4. **레디(Ready) 및 다음 단계**
   - 모든 참가자가 레디를 누르면, 방장만 “다음으로 가기” 버튼이 활성화.
   - 방장이 “다음으로 가기”를 누르면, 모든 참가자 화면이 동시에 다음 단계(배경 선택)로 이동(동기화).

---

### 1-2. 게스트 시나리오

1. **초대 수락 및 입장**
   - 알림(Notification)에서 초대를 수락하면 해당 방에 join(입장)됨.
   - 마이크/카메라는 기본적으로 켜진 상태(개별 on/off 가능).
   - 인원 수가 꽉 찬 경우, 대기실 화면에 들어오지만 “인원이 꽉 찼습니다” 모달만 보고 홈으로 돌아감(카메라/마이크 UI 없음).
   - 게스트는 초대 UI가 보이지 않음.

2. **대기실(Waiting Room)**
   - 참가자 목록, 각자의 마이크/카메라 상태, 레디(Ready) 상태가 실시간 동기화되어 표시.
   - 레디 버튼을 눌러 준비 상태를 변경할 수 있음.

3. **다음 단계 이동**
   - 방장이 “다음으로 가기”를 누르면, 모든 참가자 화면이 동시에 다음 단계(배경 선택)로 이동(동기화).

---

### 1-3. 기타 공통 사항

- **인원 제한**: 방 최대 인원 초과 시, 추가 입장자는 대기실에 들어와도 “인원이 꽉 찼습니다” 모달만 보고 홈으로 돌아가야 함.
- **동기화**: 참가자 상태(레디, 마이크/카메라, 입장/퇴장 등)는 모두 실시간 동기화.
- **초대**: 오직 이름 검색을 통한 초대만 가능(링크 초대 없음).
- **방장/게스트 UI 차이**: 방장만 초대/다음단계 버튼, 게스트는 레디만 가능.
  
---

## 2. 전체 구현 로드맵

```
Phase 1: Store 구조 설계 및 확장 (기반 작업)
    ↓
Phase 2: API 통합 및 방 정보 관리
    ↓
Phase 3: WebSocket 이벤트 핸들러 구현
    ↓
Phase 4: UI 컴포넌트 개발
    ↓
Phase 5: 통합 테스트 및 디버깅
```

---

### 2-1. Phase 1: Store 구조 설계 및 확장
#### Room Store 
기존 `useWebRTCStore`는 WebRTC 연결에 집중, 방 정보는 별도 Store로 분리

#### 1. 주요 저장 데이터

- **roomInfo**: 방의 전체 정보(방 코드, 방장, 참가자 목록, 상태 등)
- **myUserId, myNickname**: 현재 클라이언트(나)의 사용자 ID와 닉네임
- **isLoading, error**: 방 정보 관련 비동기 처리 상태 및 에러 메시지

#### 2. 주요 역할

- **방 정보 관리**
  - setRoomInfo: 서버에서 받아온 방 정보 전체를 저장
  - setMyUserId, setMyNickname: 내 정보 별도 저장

- **참가자 관리**
  - updateParticipant: 특정 참가자의 정보(ready, stream, audio/video 상태 등) 부분 업데이트
  - addParticipant, removeParticipant: 참가자 추가/제거
  - setParticipantReady, setParticipantStream, setParticipantPosition: 참가자별 ready, stream, 위치 등 개별 속성 업데이트
  - toggleParticipantAudio, toggleParticipantVideo: 참가자별 오디오/비디오 on/off 토글(내 스트림이면 실제 트랙도 제어)

- **상태 관리**
  - setLoading, setError: 로딩/에러 상태 관리
  - reset: 방 정보 및 내 정보 전체 초기화

- **계산 속성(Computed Properties)**
  - isHost: 내가 방장인지 여부
  - allReady: 모든 참가자가 ready 상태인지 여부
  - participantCount: 현재 참가자 수
  - canProceedToNext: 방장이고 모든 참가자가 ready일 때 true
  - isFull: 최대 인원(3명) 도달 여부

#### 3. 설계 전략

- **단일 소스 관리**: 방 정보와 참가자 상태를 하나의 store에서 일관성 있게 관리
- **로컬/실시간 동기화**: WebRTC/Socket 이벤트와 연동하여 참가자 상태(ready, stream, audio/video 등)를 실시간으로 반영
- **UI/비즈니스 분리**: 방 정보와 참가자 상태는 store에서 관리, UI는 store의 상태만 구독
- **확장성**: 참가자 속성, 방 속성 등 확장에 유연하게 대응할 수 있도록 설계

#### 4. 요약

- useRoomStore는 "방의 상태와 참가자 상태를 실시간으로 관리하는 중앙 저장소" 역할을 하며,
- 방 정보, 내 정보, 참가자별 상태, 계산 속성, 상태 관리 액션을 모두 포함한다.

---

#### Session Store

#### 1. 주요 저장 데이터

- **currentStage**: 현재 세션의 단계(아바타 선택, 대기실, 배경 선택, 촬영 대기, 촬영, 꾸미기, 결과)
- **roomCode**: 현재 참여 중인 방의 코드(문자열, null 가능)
- **selectedAvatarId**: 선택한 아바타의 ID(선택적)
- **selectedBackgroundId**: 선택한 배경의 ID(선택적)
- **capturedPhotos**: 촬영된 사진들의 배열(base64 또는 URL)

#### 2. 주요 역할

- **세션 단계 관리**
  - setStage: 현재 단계를 직접 설정
  - nextStage, previousStage: 단계 순서(STAGE_ORDER)에 따라 다음/이전 단계로 이동

- **방 정보 관리**
  - setRoomCode: 현재 세션의 방 코드 저장

- **아바타/배경/사진 관리**
  - setSelectedAvatar: 선택한 아바타 ID 저장
  - setSelectedBackground: 선택한 배경 ID 저장
  - addCapturedPhoto: 촬영된 사진 추가

- **상태 초기화**
  - reset: 세션 관련 모든 상태를 초기값으로 되돌림

#### 3. 설계 전략

- **단계 기반 워크플로우**: currentStage와 STAGE_ORDER를 통해 사용자의 진행 흐름을 명확하게 관리
- **세션별 데이터 분리**: 방 정보, 아바타/배경 선택, 촬영 결과 등 세션별로 필요한 데이터만 저장
- **상태 영속성**: persist 미들웨어로 새로고침/탭 닫기 후에도 세션 상태 일부 유지
- **UI/비즈니스 분리**: 세션 상태와 단계 전환 로직을 store에서 관리, UI는 store의 상태만 구독

#### 4. 요약

- useSessionStore는 "사용자의 세션 진행 단계와 세션별 주요 데이터를 관리하는 중앙 저장소" 역할을 하며,
- 단계 전환, 방 코드, 아바타/배경/사진 등 세션별 상태와 관련 액션을 모두 포함한다.
---

### 2-2. Phase 2: API 통합
#### 대기실(Waiting Room) 관련 API 모듈 정리

#### 1. roomService.ts

- **createRoom**
  - 방 생성(POST /rooms)
  - 파라미터: avatarVideoId, backgroundPreviewId(선택), totalCuts
  - 반환: roomCode(문자열)
- **getRoomInfo**
  - 방 정보 조회(GET /rooms/{roomCode})
  - 파라미터: roomCode
  - 반환: 방 정보(RoomInfo)
- **sendInvitation**
  - 방에 사용자 초대(POST /rooms/{roomCode}/invitations)
  - 파라미터: roomCode, targetMemberId
  - 반환: { invitationId, expiresAt }
  - 에러 메시지 파싱 및 예외 throw(DUPLICATE_INVITATION, ALREADY_PARTICIPANT, ROOM_FULL, UNAUTHORIZED 등)

---

#### 2. userService.ts

- **searchUsers**
  - 사용자 닉네임 검색(GET /members/search)
  - 파라미터: nickname
  - 반환: 사용자 목록({ id, nickname } 배열)

---

#### 3. notificationService.ts

- **createNotificationStream**
  - 초대 알림용 SSE(EventSource) 연결
  - 파라미터: onInvitation(콜백), onError(콜백)
  - 동작: 서버에서 invitation 이벤트 수신 시 콜백 실행
- **acceptInvitation**
  - 초대 수락(POST /invitations/{invitationId}/accept)
  - 파라미터: invitationId
  - 반환: { roomCode }
- **rejectInvitation**
  - 초대 거절(POST /invitations/{invitationId}/reject)
  - 파라미터: invitationId
  - 반환: 응답 데이터
- **getNotifications**
  - 알림 목록 조회(GET /notifications)
  - 반환: 초대 목록(Invitation[])

---

#### 4. instance.ts (공통 axios 인스턴스)

- **apiClient**
  - baseURL: VITE_API_BASE_URL 또는 http://localhost:8080/api/v1
  - withCredentials: true (쿠키 전송)
  - Request Interceptor: accessToken 쿠키에서 가져와 Authorization 헤더에 Bearer 토큰 추가
  - Response Interceptor: 401 인증 오류 시 토큰 삭제, 로그아웃, 로그인 페이지로 이동

---

#### 5. 설계 전략 요약

- **방 생성/입장/초대/알림/검색 등 대기실 관련 API를 역할별로 분리**
- **에러 처리 및 인증(토큰) 자동화**
- **SSE를 통한 실시간 초대 알림**
- **Store와 연동하여 대기실 상태 및 참가자 관리에 활용**

---

#### useRoomInfo.ts Hook 정리

#### 1. 주요 기능

- **방 정보 실시간 조회 및 동기화**
  - 특정 roomCode에 대한 방 정보를 서버에서 주기적으로 조회하여 useRoomStore에 반영
  - 초기 마운트 시 한 번, intervalMs가 0보다 크면 주기적으로 fetch

#### 2. 파라미터

- **roomCode**: 조회할 방의 코드(문자열 또는 null)
- **intervalMs**: 주기적 갱신 간격(ms, 기본값 0, 0이면 1회만 조회)

#### 3. 내부 동작

- **초기 로딩**
  - roomCode가 있으면 getRoomInfo(roomCode) 호출
  - 성공 시 setRoomInfo로 store에 저장, 실패 시 setError
  - 로딩 상태 관리: setLoading, isInitialLoading

- **주기적 갱신**
  - intervalMs > 0이면 setInterval로 fetchRoomInfo를 주기적으로 실행
  - 언마운트 시 clearInterval로 정리

- **반환값**
  - isInitialLoading: 최초 로딩 중 여부(로딩 스피너 등 UI에 활용)

#### 4. 설계 전략

- **방 정보의 최신성 보장**: 서버와 store의 상태를 주기적으로 동기화
- **로딩/에러 상태 관리**: store와 연동하여 UI에서 일관된 상태 표시 가능
- **재사용성**: 다양한 컴포넌트에서 동일한 방식으로 방 정보 조회 및 갱신 가능

#### 5. 요약

- useRoomInfo는 "방 정보의 실시간 동기화와 상태 관리를 담당하는 커스텀 훅"으로,
- 대기실, 촬영 등 방 정보가 필요한 모든 화면에서 활용할 수 있다.

---

### 2-3. Phase 3: WebSocket 이벤트 핸들러
#### useWebRTCStore.ts 설계 및 WebSocket(시그널링) 이벤트 핸들러 정리

#### 1. 주요 저장 데이터

- **myUserId, myNickname, roomCode**: 내 정보 및 현재 방 코드
- **myStream**: 내 미디어 스트림
- **peers**: 모든 Peer(참가자)의 상태(스트림, 위치, 준비 등)
- **isConnected, signalingService**: 시그널링 연결 상태 및 인스턴스
- **isAudioEnabled, isVideoEnabled**: 내 미디어 상태
- **layers**: 레이어(합성 순서 등) 정보

---

#### 2. 주요 역할

- **WebRTC 방 입장/퇴장 및 Peer 관리**
  - joinRoom, leaveRoom, addPeer, removePeer, updatePeerStream 등

- **WebRTC 시그널링 이벤트 처리**
  - createPeerConnection, handleOffer/Answer/IceCandidate 등

- **미디어/위치/준비/레이어 상태 제어 및 동기화**
  - toggleAudio/Video, sendMove, sendReady, updateLayers 등

- **범용 이벤트 전송**
  - sendToAll, sendToOthers, sendToUser

---

#### 3. WebSocket(시그널링) 이벤트 핸들러

- **join-error**
  - 방 인원 초과, 방 없음, 잘못된 상태 등 다양한 입장 실패 케이스 처리
  - alert 및 홈으로 이동

- **user-disconnected**
  - Peer 및 RoomStore에서 해당 참가자 제거

- **stage-change**
  - payload.stage에 따라 useSessionStore의 currentStage 변경(화면 동기화)

- **user-joined**
  - 새 Peer 추가, RoomStore에도 참가자 추가, PeerConnection 생성 및 Offer 전송

- **user-left**
  - Peer 및 RoomStore에서 해당 참가자 제거

- **WebRTC 시그널링 메시지**
  - offer/answer/ice-candidate: PeerConnection에 반영

- **move-updated**
  - 참가자 위치 정보 동기화

- **ready-updated**
  - 참가자 준비 상태 동기화

- **z-index-updated**
  - 레이어(합성 순서) 정보 동기화

---

#### 4. 추가/수정사항 및 설계 포인트

- **실시간 동기화**: 모든 참가자 상태(입장/퇴장, 준비, 미디어, 위치 등)를 WebSocket 이벤트로 실시간 반영
- **낙관적 업데이트**: 내 상태(ready, 미디어 등)는 WebSocket 전송과 동시에 RoomStore에도 즉시 반영
- **에러/예외 처리**: join-error 등 주요 이벤트에서 사용자 경험을 위한 alert 및 리다이렉트 처리
- **PeerConnection 관리**: Peer별로 연결/해제/스트림/ICE 등 WebRTC 연결 상태를 세밀하게 관리
- **확장성**: 범용 이벤트(sendToAll/ToOthers/ToUser)로 다양한 커스텀 동기화 시나리오 대응 가능

---

#### 5. 요약

- useWebRTCStore는 "WebRTC 및 시그널링(WebSocket) 기반의 실시간 방/참가자/미디어/상태 동기화의 중심 Store"로,
- 모든 실시간 이벤트와 Peer 상태, 미디어 제어, 동기화 로직을 통합 관리한다.
---

### 2-4. Phase 4: UI컴포넌트 개발

#### 대기실(Waiting Room) 주요 컴포넌트 정리

#### 1. PrePhotoWaitingRoom (대기실 메인)

- **역할**: 대기실 전체 UI 및 상태 관리, 입장/퇴장/레디/시작 등 주요 플로우 담당
- **주요 기능**
  - 방 정보, 참가자, 내 정보, WebRTC 연결 등 Store/Hook 연동
  - 방장/게스트 UI 분기(초대, 시작 버튼 등)
  - 참가자 비디오/오디오/레디 상태 표시 및 제어
  - 인원 초과 시 RoomFullModal 표시
  - InviteModal(초대) 오픈/클로즈 관리
  - 모든 상태 실시간 동기화(ready, 입장, 퇴장, 미디어 등)
- **주요 UI**
  - 참가자 비디오 그리드, 이름/레디/미디어 상태 표시
  - 하단 레디 버튼, 방장만 Start 버튼
  - 초대/인원초과 모달

---

#### 2. InviteModal (친구 초대 모달)

- **역할**: 닉네임 검색을 통한 참가자 초대 UI 및 로직
- **주요 기능**
  - 닉네임 입력 → 검색 → 결과 표시
  - 초대 버튼(중복 초대/참여 중/인원 초과 등 상태별 분기)
  - 초대 성공/실패 알림 및 상태 표시
  - 현재 참가자 목록 표시
  - 인원 초과 시 안내 메시지
- **주요 UI**
  - 검색 입력/버튼, 검색 결과 리스트, 초대 버튼
  - 현재 참가자 리스트, 닫기 버튼

---

#### 3. RoomFullModal (인원 초과 안내 모달)

- **역할**: 방 인원이 가득 찼을 때 진입자에게 안내 및 홈 이동 제공
- **주요 기능**
  - 인원 초과 안내 메시지
  - 홈으로 가기 버튼

---

#### 4. PrePhotoSelectAIpartner (아바타/AI 파트너 선택)

- **역할**: 대기실 입장 전 동반자(아바타) 선택 및 방 생성
- **주요 기능**
  - 아바타 카드 선택, 선택 시 강조
  - “다음으로 넘어가기” 클릭 시 방 생성 및 roomCode 저장, 대기실로 이동
  - 아바타 생성(업로드) 진입 가능
- **주요 UI**
  - 아바타 카드 리스트, 선택 강조
  - 하단 다음 버튼(선택 시 활성화)

---

#### 5. 설계 전략 요약

- **역할 분리**: 대기실 메인, 초대, 인원 초과, 아바타 선택 등 각 기능별 컴포넌트 분리
- **상태/동기화**: Store/Hook/WebRTC와 연동하여 실시간 상태 반영
- **UI/UX**: 역할별로 명확한 UI, 상태별 안내 및 예외 처리(인원 초과, 중복 초대 등)
- **확장성**: 참가자/방장/게스트/초대 등 다양한 시나리오 대응 가능 구조

---

## 3. Notification(알림) & Auth(인증) 관련 구조 정리

### 1. Notification(알림) 시스템

#### 1) notificationService.ts (API)
- **createNotificationStream**: SSE(EventSource)로 초대 알림 실시간 수신, 토큰 쿼리로 전달
- **acceptInvitation**: 초대 수락(POST /invitations/{id}/accept), roomCode 반환
- **rejectInvitation**: 초대 거절(POST /invitations/{id}/reject)
- **getNotifications**: 알림(초대) 목록 조회(GET /notifications)

#### 2) useNotificationStore.ts (Store)
- **상태**
  - invitations: 초대 알림 목록
  - eventSource: SSE 연결 객체
  - isConnected: SSE 연결 여부
  - isLoading: 알림 목록 로딩 상태
- **액션**
  - connect/disconnect: SSE 연결/해제
  - addInvitation/removeInvitation: 초대 추가/제거
  - accept/reject: 초대 수락/거절(알림 목록에서 제거)
  - fetchInvitations: 알림 목록 조회
  - getUnreadCount: 읽지 않은 알림 수 반환

#### 3) NotificationBadge.tsx (UI)
- **알림 아이콘 + 뱃지**: 읽지 않은 초대 수 표시, 클릭 시 NotificationPanel 오픈
- **SSE 연결/해제**: 컴포넌트 마운트/언마운트 시 자동 관리

#### 4) NotificationPanel.tsx (UI)
- **알림 목록 표시**: 초대 수락/거절 버튼, 수락 시 대기실로 이동
- **로딩/빈 목록/에러 처리**: 상태별 안내 메시지

---

### 2. Auth(인증) 시스템

#### 1) useAuthStore.ts (Store)
- **상태**
  - isAuthenticated: 인증 여부
  - user: 현재 로그인 사용자 정보
  - isLoading, error: 인증 관련 로딩/에러 상태
- **액션**
  - setAuth: 토큰+유저 정보 저장(쿠키에 토큰 저장)
  - logout: 토큰 삭제, 상태 초기화, 관련 Store(방/세션/WebRTC)도 초기화
  - setUser/setLoading/setError: 개별 상태 변경
  - getToken/hasValidToken: 토큰 조회/유효성 체크
  - mockLogin: 개발용 Mock 로그인

#### 2) 인증 흐름
- **apiClient(axios 인스턴스)**
  - 모든 요청에 accessToken을 Authorization 헤더로 자동 추가
  - 401 응답 시 토큰 삭제, 로그아웃, 로그인 페이지로 이동

---

### 3. 설계 전략 요약

- **실시간 알림**: SSE 기반 초대 알림, Store와 UI에서 실시간 반영
- **초대 수락/거절**: 알림 패널에서 바로 처리, 수락 시 대기실로 이동
- **인증/토큰 관리**: 쿠키+Store 이중 관리, 자동 헤더/에러 처리
- **상태 일관성**: 인증/알림/방/세션 등 Store 간 상태 동기화 및 초기화
- **확장성**: 초대 외 다양한 알림, 인증 확장에 유연하게 대응 가능

