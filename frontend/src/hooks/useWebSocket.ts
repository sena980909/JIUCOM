import { useState, useEffect, useRef, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import type { Notification } from '../api/notifications';

interface WebSocketNotification extends Notification {
  _wsId: string;
}

export function useWebSocket(accessToken: string | null) {
  const [notifications, setNotifications] = useState<WebSocketNotification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const clientRef = useRef<Client | null>(null);

  useEffect(() => {
    if (!accessToken) {
      if (clientRef.current) {
        clientRef.current.deactivate();
        clientRef.current = null;
      }
      setNotifications([]);
      setUnreadCount(0);
      return;
    }

    const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';
    const wsUrl = `${baseUrl}/ws`;

    const client = new Client({
      webSocketFactory: () => new SockJS(wsUrl),
      connectHeaders: {
        Authorization: `Bearer ${accessToken}`,
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      onConnect: () => {
        client.subscribe('/user/queue/notifications', (message) => {
          try {
            const notification = JSON.parse(message.body) as Notification;
            const wsNotification: WebSocketNotification = {
              ...notification,
              _wsId: `${Date.now()}-${Math.random()}`,
            };
            setNotifications((prev) => [wsNotification, ...prev]);
            if (!notification.read) {
              setUnreadCount((prev) => prev + 1);
            }
          } catch {
            // Ignore malformed messages
          }
        });
      },
      onStompError: (frame) => {
        console.error('STOMP error:', frame.headers['message']);
      },
    });

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
      clientRef.current = null;
    };
  }, [accessToken]);

  const clearNotification = useCallback((id: number) => {
    setNotifications((prev) => prev.filter((n) => n.id !== id));
    setUnreadCount((prev) => Math.max(0, prev - 1));
  }, []);

  return {
    notifications,
    unreadCount,
    setUnreadCount,
    clearNotification,
  };
}
