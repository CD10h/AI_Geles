<script>
  import { setContext } from "svelte";
  import { notificationContextKey } from "./contexts";
  import { AppNotificationType } from "./enums";
  import Notifications from "./Notifications.svelte";

  let lastId = 0;
  let notifications: AppNotification[] = [];

  function handleRemoveNotification(id: number) {
    const index = notifications.findIndex(
      notification => notification.id === id
    );
    if (index === -1) {
      return;
    }
    notifications = [
      ...notifications.slice(0, index),
      ...notifications.slice(index + 1)
    ];
  }

  function addNotification(
    text: string,
    type: AppNotificationType,
    timeout?: number
  ) {
    const id = ++lastId;

    notifications = [...notifications, { id, text, type }];
    setTimeout(() => {
      handleRemoveNotification(id);
    }, timeout ?? 5000);
    return id;
  }

  function addLoadingNotification<T>(text: string, promise: Promise<T>) {
    const id = ++lastId;

    setTimeout(() => {
      notifications = [
        ...notifications,
        { id, text, loading: true, type: AppNotificationType.INFO }
      ];
      promise.finally(() => {
        handleRemoveNotification(id);
      });
    }, 150);
    return promise;
  }

  function removeNotification(id: number) {
    handleRemoveNotification(id);
    return true;
  }

  let context: AppNotificationContext = {
    addNotification,
    addLoadingNotification,
    removeNotification
  };

  setContext(notificationContextKey, context);
</script>

<Notifications {notifications} />
<slot />
