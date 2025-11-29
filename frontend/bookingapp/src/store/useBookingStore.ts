import { create } from "zustand";
import {
    BookingCreateRequest,
    BookingResponseDto,
    BookingStatus,
} from "../types";
import {
    approveBookingRequest,
    createBookingRequest,
    getBookingRequests,
    rejectBookingRequest,
} from "../api/bookingRequestsApi";

interface BookingState {
    bookings: BookingResponseDto[];
    loading: boolean;
    error: string | null;
    filterStatus: BookingStatus | "ALL";

    setFilterStatus: (status: BookingStatus | "ALL") => void;
    fetchBookings: () => Promise<void>;
    createBooking: (payload: BookingCreateRequest) => Promise<BookingResponseDto>;
    updateStatus: (
        uuid: string,
        action: "approve" | "reject"
    ) => Promise<BookingResponseDto>;
}

export const useBookingStore = create<BookingState>((set, get) => ({
    bookings: [],
    loading: false,
    error: null,
    filterStatus: "ALL",

    setFilterStatus: (status) => set({ filterStatus: status }),

    fetchBookings: async () => {
        const { filterStatus } = get();
        set({ loading: true, error: null });
        try {
            const bookings = await getBookingRequests(
                filterStatus === "ALL" ? undefined : filterStatus
            );
            set({ bookings });
        } catch (e: any) {
            set({
                error: e?.message || "Failed to fetch bookings",
                bookings: [],
            });
        } finally {
            set({ loading: false });
        }
    },

    createBooking: async (payload) => {
        const booking = await createBookingRequest(payload);
        set((state) => ({ bookings: [...state.bookings, booking] }));
        return booking;
    },

    updateStatus: async (uuid, action) => {
        try {
            const fn = action === "approve" ? approveBookingRequest : rejectBookingRequest;
            const updated = await fn(uuid);

            set((state) => ({
                bookings: state.bookings.map((b) => (b.uuid === uuid ? updated : b)),
            }));

            return updated;
        } catch (err: any) {
            // pass the backend error to UI
            throw new Error(err.message || "Failed to update booking status");
        }
    },

}));
