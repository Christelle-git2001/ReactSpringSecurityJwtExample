import { create } from 'zustand';

export const useNotificationTrigger = create((set) => ({
    refreshKey: 0,
    triggerRefresh: () => set((state) => ({ refreshKey: state.refreshKey + 1 })),
}));