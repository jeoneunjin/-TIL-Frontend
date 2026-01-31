# [React] 대기실 구현 계획(WebRTC+WebSocket+API 통합)

> **Date:** 2026-01-31  
> **Tag:** #Frontend #WebRTC #WebSocket #API #TIL

---

## 📋 목차
1. [예상 시나리오](#1-예상-시나리오)
   - [방장 시나리오](#1-1-방장-시나리오)
   - [게스트 시나리오](#1-2-게스트-시나리오)
2. [전체 구현 로드맵](#2-전체-구현-로드맵)
   - [Phase 1: Store 구조 설계 및 확장](#2-1-Phase-1-Store-구조-설계-및-확장)
   - [Phase 2: API 통합 및 방 정보 관리](#2-2-Phase-2-API-통합)
   - [Phase 3: WebSocket 이벤트 핸들러 구현](#2-3-Phase-3-WebSocket-이벤트-핸들러-구현)
   - [Phase 4: UI 컴포넌트 개발](#2-4-Phase-4-UI-컴포넌트-개발) 

---

## 1. 예상 시나리오
방장과 게스트로 나뉘어진다.

### 1-1. 방장 시나리오 

1. 방장 입장(이전 화면(아바타 선택 화면)에서 "다음으로 넘어가기" 버튼을 눌렀을 때
- 방장이 입장하려 할 때 방 생성이 되어야 하고
- 방장 입장(join)
- 마이크 카메라 설정(디폴트는 둘 다 켜져 있게?)
  - UI에서 마이크/카메라 설정 가능하게 해야 함 

2. 친구 초대하기(2가지 방법이 있음)
- 첫 번째 방법: **방 링크 보내기** 
- 두 번째 방법: **초대 보내기**
  - 이름 검색(검색 API 호출)
  - 선택
  - 초대 보내기((초대 API 호출))
    - 초대 받은 사람은 사이트에서 알림이 옴
> 인원 수가 꽉찬 방은 못 들어옴 -> 대기실 화면은 뜨나 모달창으로 인원이 꽉 찼다고 못 들어온다고 알려줘야 함

3. 레디 상태
- 대기실에서 사람들이 레디를 눌러야 함
- 이때 서로 레디 상태인지 알 수 있게 동기화가 되어야함-> 화면에 표시해야되기 때문

4. 대기실에서 다음단계(배경 선택 화면)으로 넘어가기 
- 모든 사람이 레디가 되면 방장이 "다음으로 가기" 버튼을 눌러야 다음으로 넘어갈 수 있음
- 다음 단계로 넘어가는 것 또한 동기화 필요 화면이 같이 넘어가야함

---

### 1-2. 게스트 시나리오

1. 게스트 입장
- 방장이 보낸 초대 또는 받은 링크를 통해 들어오게 됨(join)
- 게스트 또한 마이크 카메라를 켜져 있는게 디폴트
  - UI에서 마이크/가메라 설정 가능하게 할거임
- 이때 인원 수가 꽉찼다면 대기실 화면에는 들어와지나 모달창으로 인원이 꽉 찼다고(백엔드에서 join 웹소켓 핸들러를 통해 예외처리 해줄거임)알려주고 모달창에는 홈으로 가기만 선택할 수 있음(이 인물은 화면에 카메라 표시 X 마이크 X)
- 게스트는 방장과 다르게 초대는 못 하기 때문에 초대 UI는 숨겨둘 예정

2. 레디 상태
- 레디를 누를 수 있음(UI) 
- 게스트가 누른 레디는 나머지 사람도 알 수 있게 동기화 되어야 함

3. 다음단계로 넘어가기
- 방장이 "다음으로 가기" 버튼을 넘어가면 다음 화면인 배경 선택 화면으로 같이 넘어감

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
#### Room Store 생성 
기존 `useWebRTCStore`는 WebRTC 연결에 집중, 방 정보는 별도 Store로 분리

```typescript
// src/stores/useRoomStore.ts
import { create } from 'zustand';
import { devtools } from 'zustand/middleware';

export interface Participant {
  id: string;
  nickname: string;
  isHost: boolean;
  ready: boolean;
  connected: boolean;
  
  // WebRTC 관련 (useWebRTCStore와 연동)
  stream?: MediaStream;
  audioEnabled?: boolean;
  videoEnabled?: boolean;
}

interface RoomInfo {
  roomId: string;
  roomCode: string;
  hostId: string;
  avatarVideoId?: string;
  backgroundPreviewId?: string;
  status: 'WAITING' | 'SELECTING_BG' | 'IN_BOOTH' | 'DECORATING' | 'COMPLETE';
  totalCuts: number;
  createdAt: string;
  participants: Participant[];
}

interface RoomState {
  // 방 정보
  roomInfo: RoomInfo | null;
  
  // 현재 사용자 정보
  myUserId: string | null;
  isHost: boolean;
  
  // 로딩 상태
  isLoading: boolean;
  error: string | null;
  
  // Actions
  setRoomInfo: (info: RoomInfo) => void;
  setMyUserId: (userId: string) => void;
  updateParticipant: (userId: string, updates: Partial<Participant>) => void;
  addParticipant: (participant: Participant) => void;
  removeParticipant: (userId: string) => void;
  setParticipantReady: (userId: string, ready: boolean) => void;
  
  // Computed
  allReady: () => boolean;
  participantCount: () => number;
  canProceedToNext: () => boolean;
  
  // Reset
  reset: () => void;
}

export const useRoomStore = create<RoomState>()(
  devtools(
    (set, get) => ({
      roomInfo: null,
      myUserId: null,
      isHost: false,
      isLoading: false,
      error: null,

      setRoomInfo: (info) => {
        const myUserId = get().myUserId;
        const isHost = myUserId ? info.hostId === myUserId : false;
        
        set({ 
          roomInfo: info, 
          isHost,
          error: null 
        });
      },

      setMyUserId: (userId) => {
        set({ myUserId: userId });
        
        // 방 정보가 이미 있으면 isHost 재계산
        const roomInfo = get().roomInfo;
        if (roomInfo) {
          set({ isHost: roomInfo.hostId === userId });
        }
      },

      updateParticipant: (userId, updates) => {
        set((state) => {
          if (!state.roomInfo) return state;
          
          const participants = state.roomInfo.participants.map((p) =>
            p.id === userId ? { ...p, ...updates } : p
          );
          
          return {
            roomInfo: {
              ...state.roomInfo,
              participants,
            },
          };
        });
      },

      addParticipant: (participant) => {
        set((state) => {
          if (!state.roomInfo) return state;
          
          // 이미 존재하는지 체크
          const exists = state.roomInfo.participants.some(
            (p) => p.id === participant.id
          );
          
          if (exists) return state;
          
          return {
            roomInfo: {
              ...state.roomInfo,
              participants: [...state.roomInfo.participants, participant],
            },
          };
        });
      },

      removeParticipant: (userId) => {
        set((state) => {
          if (!state.roomInfo) return state;
          
          return {
            roomInfo: {
              ...state.roomInfo,
              participants: state.roomInfo.participants.filter(
                (p) => p.id !== userId
              ),
            },
          };
        });
      },

      setParticipantReady: (userId, ready) => {
        get().updateParticipant(userId, { ready });
      },

      allReady: () => {
        const roomInfo = get().roomInfo;
        if (!roomInfo || roomInfo.participants.length === 0) return false;
        
        return roomInfo.participants.every((p) => p.ready);
      },

      participantCount: () => {
        return get().roomInfo?.participants.length || 0;
      },

      canProceedToNext: () => {
        const { isHost, allReady } = get();
        return isHost && allReady();
      },

      reset: () => {
        set({
          roomInfo: null,
          myUserId: null,
          isHost: false,
          isLoading: false,
          error: null,
        });
      },
    }),
    { name: 'RoomStore' }
  )
);
```

---

#### Session Store

```typescript
// src/stores/useSessionStore.ts
import { create } from 'zustand';
import { devtools, persist } from 'zustand/middleware';

export type SessionStage = 
  | 'select-avatar'
  | 'waiting-room'
  | 'select-bg'
  | 'waiting-booth'
  | 'booth'
  | 'decorate'
  | 'result';

interface SessionState {
  currentStage: SessionStage;
  roomCode: string | null;
  
  // 단계별 데이터
  selectedAvatar?: string;
  selectedBackground?: {
    id: string;
    type: 'color' | 'upload';
    data: string;
  };
  capturedPhotos: string[];
  
  // Actions
  setStage: (stage: SessionStage) => void;
  setRoomCode: (code: string) => void;
  nextStage: () => void;
  updateStageData: (data: any) => void;
  reset: () => void;
}

const STAGE_ORDER: SessionStage[] = [
  'select-avatar',
  'waiting-room',
  'select-bg',
  'waiting-booth',
  'booth',
  'decorate',
  'result',
];

export const useSessionStore = create<SessionState>()(
  devtools(
    persist(
      (set, get) => ({
        currentStage: 'select-avatar',
        roomCode: null,
        capturedPhotos: [],

        setStage: (stage) => set({ currentStage: stage }),
        
        setRoomCode: (code) => set({ roomCode: code }),

        nextStage: () => {
          const currentIndex = STAGE_ORDER.indexOf(get().currentStage);
          if (currentIndex < STAGE_ORDER.length - 1) {
            set({ currentStage: STAGE_ORDER[currentIndex + 1] });
          }
        },

        updateStageData: (data) => set(data),

        reset: () => {
          set({
            currentStage: 'select-avatar',
            roomCode: null,
            selectedAvatar: undefined,
            selectedBackground: undefined,
            capturedPhotos: [],
          });
        },
      }),
      { name: 'session-storage' }
    ),
    { name: 'SessionStore' }
  )
);
```

---

### 2-2. Phase 2: API 통합
#### Room API Service 확장

```typescript
// src/api/roomService.ts (기존 파일 확장)

// 기존 createRoom은 그대로 유지하고 아래 함수들 추가

/**
 * 방 정보 조회
 */
export const getRoomInfo = async (roomCode: string) => {
  try {
    const response = await apiClient.get(`/rooms/${roomCode}`);
    return response.data.data;
  } catch (error) {
    console.error('방 정보 조회 실패:', error);
    throw error;
  }
};

/**
 * 사용자 검색
 */
export const searchUsers = async (query: string) => {
  try {
    const response = await apiClient.get('/users/search', {
      params: { query },
    });
    return response.data.data;
  } catch (error) {
    console.error('사용자 검색 실패:', error);
    throw error;
  }
};

/**
 * 초대 보내기
 */
export const sendInvitation = async (roomCode: string, targetUserId: string) => {
  try {
    const response = await apiClient.post(`/rooms/${roomCode}/invitations`, {
      targetUserId,
    });
    return response.data;
  } catch (error) {
    console.error('초대 보내기 실패:', error);
    throw error;
  }
};
```

---

#### Hooks 생성

```typescript
// src/hooks/useRoomInfo.ts
import { useEffect } from 'react';
import { useRoomStore } from '@/stores/useRoomStore';
import { getRoomInfo } from '@/api/roomService';

export const useRoomInfo = (roomCode: string | null) => {
  const { setRoomInfo, setMyUserId } = useRoomStore();
  const myUserId = useRoomStore((s) => s.myUserId);

  useEffect(() => {
    if (!roomCode) return;

    const fetchRoomInfo = async () => {
      try {
        const data = await getRoomInfo(roomCode);
        setRoomInfo(data);
      } catch (error) {
        console.error('방 정보 불러오기 실패:', error);
      }
    };

    fetchRoomInfo();

    // 주기적으로 갱신 (선택사항)
    const interval = setInterval(fetchRoomInfo, 10000); // 10초마다

    return () => clearInterval(interval);
  }, [roomCode, setRoomInfo]);

  return { roomInfo: useRoomStore((s) => s.roomInfo) };
};
```

---

### 2-3. Phase 3: WebSocket 이벤트 핸들러
#### useWebRTCStore 확장

기존 `useWebRTCStore`에 대기실용 핸들러 추가

```typescript
// src/stores/useWebRTCStore.ts (기존 파일 수정)

// 기존 store에 아래 메서드들 추가

interface WebRTCStore {
  // ... 기존 필드들
  
  // 추가: 준비 상태
  myReady: boolean;
  setMyReady: (ready: boolean) => void;
  
  // 추가: 단계 전환
  broadcastStageChange: (stage: string) => void;
  
  // ... 기존 메서드들
}

// store 구현부에 추가
export const useWebRTCStore = create<WebRTCStore>()((set, get) => ({
  // ... 기존 상태들
  
  myReady: false,
  
  setMyReady: (ready: boolean) => {
    set({ myReady: ready });
    const socket = get().socket;
    if (socket) {
      socket.emit('ready', { ready });
    }
  },
  
  broadcastStageChange: (stage: string) => {
    const socket = get().socket;
    if (socket) {
      socket.emit('stage-change', { stage });
    }
  },
  
  // ... 기존 메서드들
}));
```

---

#### WebSocket 이벤트 리스너 추가

```typescript
// src/stores/useWebRTCStore.ts의 joinRoom 함수 내부 수정

// 기존 이벤트 리스너들 유지하고 아래 추가

socket.on('user-joined', (data: { userId: string; nickname: string }) => {
  console.log('새 유저 입장:', data);
  
  // RoomStore에 참가자 추가
  const { addParticipant } = useRoomStore.getState();
  addParticipant({
    id: data.userId,
    nickname: data.nickname,
    isHost: false,
    ready: false,
    connected: true,
  });
});

socket.on('user-left', (data: { userId: string }) => {
  console.log('유저 퇴장:', data);
  
  // RoomStore에서 참가자 제거
  const { removeParticipant } = useRoomStore.getState();
  removeParticipant(data.userId);
  
  // Peer 연결 정리
  const peer = get().peers[data.userId];
  if (peer?.connection) {
    peer.connection.close();
  }
  
  set((state) => {
    const newPeers = { ...state.peers };
    delete newPeers[data.userId];
    return { peers: newPeers };
  });
});

socket.on('ready-status', (data: { userId: string; ready: boolean }) => {
  console.log('준비 상태 변경:', data);
  
  const { setParticipantReady } = useRoomStore.getState();
  setParticipantReady(data.userId, data.ready);
});

socket.on('stage-change', (data: { stage: string }) => {
  console.log('단계 변경:', data);
  
  const { setStage } = useSessionStore.getState();
  setStage(data.stage as any);
  
  // 페이지 이동은 컴포넌트에서 처리
});

socket.on('room-full', () => {
  console.log('방 인원 초과');
  alert('방 인원이 가득 찼습니다.');
  // 컴포넌트에서 모달 띄우기 위한 상태 업데이트
  set({ error: 'ROOM_FULL' });
});
```

---

### 2-4. Phase 4: UI컴포넌트 개발

#### 대기실 메인 컴포넌트 구조 

```typescript
// src/pages/prePhoto_2_WaitingRoom.tsx (기존 파일 대폭 수정)

import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useRoomStore } from '@/stores/useRoomStore';
import { useSessionStore } from '@/stores/useSessionStore';
import { useWebRTCStore } from '@/stores/useWebRTCStore';
import { useMediaStream } from '@/hooks/useMediaStream';
import { useRoomInfo } from '@/hooks/useRoomInfo';

import { ParticipantGrid } from '@/components/waitingRoom/ParticipantGrid';
import { InvitePanel } from '@/components/waitingRoom/InvitePanel';
import { RoomFullModal } from '@/components/waitingRoom/RoomFullModal';

export const WaitingRoom: React.FC = () => {
  const navigate = useNavigate();
  
  // Stores
  const { roomInfo, isHost, myUserId, allReady, canProceedToNext } = useRoomStore();
  const { roomCode, nextStage } = useSessionStore();
  const { 
    isConnected, 
    myReady, 
    setMyReady, 
    joinRoom, 
    leaveRoom,
    broadcastStageChange 
  } = useWebRTCStore();
  
  // Media
  const { stream, startStream } = useMediaStream();
  
  // Room info polling
  useRoomInfo(roomCode);
  
  // States
  const [isRoomFull, setIsRoomFull] = useState(false);
  const [showInviteModal, setShowInviteModal] = useState(false);

  /**
   * 초기 설정
   */
  useEffect(() => {
    const initialize = async () => {
      // 1. 카메라/마이크 시작
      try {
        await startStream();
      } catch (error) {
        console.error('미디어 스트림 시작 실패:', error);
      }

      // 2. WebSocket 방 입장
      if (roomCode && myUserId) {
        try {
          await joinRoom(roomCode, myUserId, '사용자'); // 실제로는 nickname도 state에서 가져와야 함
        } catch (error) {
          console.error('방 입장 실패:', error);
        }
      }
    };

    initialize();

    // Cleanup
    return () => {
      leaveRoom();
    };
  }, []);

  /**
   * 방 인원 초과 체크
   */
  useEffect(() => {
    if (roomInfo && roomInfo.participants.length >= 4) { // 최대 인원 체크
      const isMe = roomInfo.participants.some((p) => p.id === myUserId);
      if (!isMe) {
        setIsRoomFull(true);
      }
    }
  }, [roomInfo, myUserId]);

  /**
   * 준비 토글
   */
  const handleReadyToggle = () => {
    setMyReady(!myReady);
  };

  /**
   * 다음 단계로 이동 (방장만)
   */
  const handleProceedToNext = () => {
    if (!canProceedToNext()) return;

    // 1. 세션 스토어 업데이트
    nextStage();

    // 2. WebSocket으로 브로드캐스트
    broadcastStageChange('select-bg');

    // 3. 페이지 이동
    navigate('/pre-photo/select-bg');
  };

  if (!roomInfo) {
    return (
      <div className="min-h-screen bg-gray-900 flex items-center justify-center">
        <div className="text-white text-xl">방 정보를 불러오는 중...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-900 p-6">
      {/* 헤더 */}
      <header className="mb-8">
        <h1 className="text-3xl font-bold text-white mb-2">대기실</h1>
        <div className="flex items-center gap-4 text-gray-400">
          <span>방 코드: {roomCode}</span>
          <span>참가자: {roomInfo.participants.length}명</span>
          {isHost && <span className="text-yellow-400">👑 방장</span>}
        </div>
      </header>

      {/* 참가자 그리드 */}
      <ParticipantGrid
        participants={roomInfo.participants}
        myUserId={myUserId || ''}
      />

      {/* 하단 컨트롤 */}
      <footer className="fixed bottom-0 left-0 right-0 bg-gray-800 p-6 border-t border-gray-700">
        <div className="max-w-7xl mx-auto flex items-center justify-between">
          {/* 왼쪽: 초대 버튼 (방장만) */}
          <div>
            {isHost && (
              <button
                onClick={() => setShowInviteModal(true)}
                className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
              >
                👥 친구 초대하기
              </button>
            )}
          </div>

          {/* 중앙: 준비 버튼 */}
          <div>
            <button
              onClick={handleReadyToggle}
              className={`px-8 py-3 rounded-lg font-semibold transition-colors ${
                myReady
                  ? 'bg-green-600 text-white hover:bg-green-700'
                  : 'bg-gray-600 text-white hover:bg-gray-700'
              }`}
            >
              {myReady ? '✓ 준비 완료' : '준비하기'}
            </button>
          </div>

          {/* 오른쪽: 다음 단계 버튼 (방장만, 모두 준비 시) */}
          <div>
            {isHost && (
              <button
                onClick={handleProceedToNext}
                disabled={!allReady()}
                className={`px-6 py-3 rounded-lg font-semibold transition-colors ${
                  allReady()
                    ? 'bg-purple-600 text-white hover:bg-purple-700'
                    : 'bg-gray-600 text-gray-400 cursor-not-allowed'
                }`}
              >
                다음 단계로 →
              </button>
            )}
          </div>
        </div>
      </footer>

      {/* 모달들 */}
      {showInviteModal && (
        <InvitePanel
          roomCode={roomCode || ''}
          onClose={() => setShowInviteModal(false)}
        />
      )}

      {isRoomFull && (
        <RoomFullModal onGoHome={() => navigate('/')} />
      )}
    </div>
  );
};
```

---

#### 참가자 그리드 컴포넌트 

```typescript
// src/components/waitingRoom/ParticipantGrid.tsx

import React from 'react';
import { useWebRTCStore } from '@/stores/useWebRTCStore';
import type { Participant } from '@/stores/useRoomStore';

interface ParticipantGridProps {
  participants: Participant[];
  myUserId: string;
}

export const ParticipantGrid: React.FC<ParticipantGridProps> = ({
  participants,
  myUserId,
}) => {
  const { peers } = useWebRTCStore();

  return (
    <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6 mb-8">
      {participants.map((participant) => {
        const isMe = participant.id === myUserId;
        const peer = peers[participant.id];
        const stream = isMe ? useWebRTCStore.getState().myStream : peer?.stream;

        return (
          <ParticipantCard
            key={participant.id}
            participant={participant}
            stream={stream}
            isMe={isMe}
          />
        );
      })}
    </div>
  );
};

interface ParticipantCardProps {
  participant: Participant;
  stream?: MediaStream;
  isMe: boolean;
}

const ParticipantCard: React.FC<ParticipantCardProps> = ({
  participant,
  stream,
  isMe,
}) => {
  return (
    <div className="relative bg-gray-800 rounded-xl overflow-hidden aspect-video">
      {/* 비디오 */}
      {stream ? (
        <video
          ref={(el) => {
            if (el && stream) {
              el.srcObject = stream;
            }
          }}
          autoPlay
          playsInline
          muted={isMe}
          className="w-full h-full object-cover"
        />
      ) : (
        <div className="w-full h-full flex items-center justify-center bg-gray-700">
          <div className="text-6xl">👤</div>
        </div>
      )}

      {/* 정보 오버레이 */}
      <div className="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/80 to-transparent p-4">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-white font-semibold">
              {participant.nickname}
              {isMe && ' (나)'}
              {participant.isHost && ' 👑'}
            </p>
          </div>
          <div>
            {participant.ready ? (
              <span className="text-green-400 text-sm">✓ 준비됨</span>
            ) : (
              <span className="text-gray-400 text-sm">대기 중</span>
            )}
          </div>
        </div>
      </div>

      {/* 연결 상태 */}
      {!participant.connected && (
        <div className="absolute inset-0 bg-black/50 flex items-center justify-center">
          <p className="text-white">연결 끊김...</p>
        </div>
      )}
    </div>
  );
};
```

---

#### 초대 패널 컴포넌트

```typescript
// src/components/waitingRoom/InvitePanel.tsx

import React, { useState } from 'react';
import { searchUsers, sendInvitation } from '@/api/roomService';

interface InvitePanelProps {
  roomCode: string;
  onClose: () => void;
}

export const InvitePanel: React.FC<InvitePanelProps> = ({ roomCode, onClose }) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchResults, setSearchResults] = useState<any[]>([]);
  const [isSearching, setIsSearching] = useState(false);

  const roomLink = `${window.location.origin}/join/${roomCode}`;

  const handleCopyLink = () => {
    navigator.clipboard.writeText(roomLink);
    alert('링크가 복사되었습니다!');
  };

  const handleSearch = async () => {
    if (!searchQuery.trim()) return;

    setIsSearching(true);
    try {
      const results = await searchUsers(searchQuery);
      setSearchResults(results);
    } catch (error) {
      console.error('검색 실패:', error);
      alert('사용자 검색에 실패했습니다.');
    } finally {
      setIsSearching(false);
    }
  };

  const handleInvite = async (userId: string) => {
    try {
      await sendInvitation(roomCode, userId);
      alert('초대를 보냈습니다!');
    } catch (error) {
      console.error('초대 실패:', error);
      alert('초대 보내기에 실패했습니다.');
    }
  };

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div className="bg-gray-800 rounded-xl p-6 w-full max-w-md">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-bold text-white">친구 초대하기</h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-white text-2xl"
          >
            ×
          </button>
        </div>

        {/* 방법 1: 링크 공유 */}
        <section className="mb-6">
          <h3 className="text-lg font-semibold text-white mb-3">
            방법 1: 링크 공유
          </h3>
          <div className="flex gap-2">
            <input
              type="text"
              value={roomLink}
              readOnly
              className="flex-1 px-4 py-2 bg-gray-700 text-white rounded-lg"
            />
            <button
              onClick={handleCopyLink}
              className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
            >
              복사
            </button>
          </div>
        </section>

        {/* 방법 2: 이름 검색 */}
        <section>
          <h3 className="text-lg font-semibold text-white mb-3">
            방법 2: 이름 검색
          </h3>
          <div className="flex gap-2 mb-4">
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
              placeholder="닉네임 검색..."
              className="flex-1 px-4 py-2 bg-gray-700 text-white rounded-lg"
            />
            <button
              onClick={handleSearch}
              disabled={isSearching}
              className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
            >
              {isSearching ? '검색 중...' : '검색'}
            </button>
          </div>

          {/* 검색 결과 */}
          {searchResults.length > 0 && (
            <div className="space-y-2">
              {searchResults.map((user) => (
                <div
                  key={user.id}
                  className="flex items-center justify-between p-3 bg-gray-700 rounded-lg"
                >
                  <span className="text-white">{user.nickname}</span>
                  <button
                    onClick={() => handleInvite(user.id)}
                    className="px-3 py-1 bg-purple-600 text-white rounded hover:bg-purple-700"
                  >
                    초대
                  </button>
                </div>
              ))}
            </div>
          )}
        </section>
      </div>
    </div>
  );
};
```

---

#### 방인원 초과 모달

```typescript
// src/components/waitingRoom/RoomFullModal.tsx

import React from 'react';

interface RoomFullModalProps {
  onGoHome: () => void;
}

export const RoomFullModal: React.FC<RoomFullModalProps> = ({ onGoHome }) => {
  return (
    <div className="fixed inset-0 bg-black/70 flex items-center justify-center z-50">
      <div className="bg-gray-800 rounded-xl p-8 max-w-md text-center">
        <div className="text-6xl mb-4">🚫</div>
        <h2 className="text-2xl font-bold text-white mb-4">
          방 인원이 가득 찼습니다
        </h2>
        <p className="text-gray-400 mb-6">
          현재 방에 더 이상 입장할 수 없습니다.
          <br />
          다른 방을 찾아보시거나 새로운 방을 만들어주세요.
        </p>
        <button
          onClick={onGoHome}
          className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          홈으로 가기
        </button>
      </div>
    </div>
  );
};
```



