import {
  BookingCreateRequest,
  BookingResponseDto,
  BookingStatus,
} from "../types";

const API_BASE_URL =
  process.env.REACT_APP_API_BASE_URL ?? "http://localhost:8080";

export async function createBookingRequest(
  payload: BookingCreateRequest
): Promise<BookingResponseDto> {
  const res = await fetch(`${API_BASE_URL}/api/v1/booking-requests`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || "Failed to create booking");
  }

  return res.json();
}

export async function getBookingRequests(
  status?: BookingStatus
): Promise<BookingResponseDto[]> {
  const params = new URLSearchParams();
  if (status) params.append("status", status);
  const query = params.toString();

  const res = await fetch(
    `${API_BASE_URL}/api/v1/booking-requests${query ? `?${query}` : ""}`
  );
  if (!res.ok) {
    throw new Error("Failed to fetch bookings");
  }
  return res.json();
}

export async function getBookingRequestById(
  id: string
): Promise<BookingResponseDto> {
  const res = await fetch(`${API_BASE_URL}/api/v1/booking-requests/${id}`);
  if (!res.ok) {
    throw new Error("Booking not found");
  }
  return res.json();
}

// src/api/bookingRequestsApi.ts
export async function approveBookingRequest(uuid: string) {
  const res = await fetch(
    `${API_BASE_URL}/api/v1/booking-requests/${uuid}/approve`,
    { method: "PATCH" }
  );

  if (!res.ok) {
    const text = await res.text();

    if (res.status === 409) {
      // conflict (overlapping, invalid transition)
      throw new Error(text || "Conflict: cannot approve this booking");
    }

    throw new Error(text || "Failed to approve booking");
  }

  return res.json();
}

export async function rejectBookingRequest(uuid: string) {
  const res = await fetch(
    `${API_BASE_URL}/api/v1/booking-requests/${uuid}/reject`,
    { method: "PATCH" }
  );

  if (!res.ok) {
    const text = await res.text();

    if (res.status === 409) {
      throw new Error(text || "Conflict: cannot reject this booking");
    }

    throw new Error(text || "Failed to reject booking");
  }

  return res.json();
}

