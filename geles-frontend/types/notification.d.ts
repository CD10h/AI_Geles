interface AppNotification {
  id: number;
  text: string;
  type: AppNotificationType;
  loading?: boolean;
}

interface AppNotificationContext {
  addNotification(
    text: string,
    type: AppNotificationType,
    timeout?: number
  ): number;
  addLoadingNotification(text: string, promise: Promise<T>): Promise<T>;
  removeNotification(id: number): boolean;
}
