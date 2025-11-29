export type AdSpaceType =
  | "BILLBOARD"
  | "BUS_STOP"
  | "MALL_DISPLAY"
  | "TRANSIT_AD";

export const AD_SPACE_TYPES: AdSpaceType[] = [
  "BILLBOARD",
  "BUS_STOP",
  "MALL_DISPLAY",
  "TRANSIT_AD",
];

export type AdSpaceAvailabilityStatus =
  | "AVAILABLE"
  | "BOOKED"
  | "MAINTENANCE";

export interface AdSpaceResponseDto {
  uuid: string;
  name: string;
  type: AdSpaceType;
  city: string;
  address: string;
  pricePerDay: number;
  status: AdSpaceAvailabilityStatus; // or availabilityStatus from BE, handled in UI
}

// --- Bookings ---

export type BookingStatus = "PENDING" | "APPROVED" | "REJECTED";

// must match BookingRequestCreateDto: adSpaceUuid + dates
export interface BookingCreateRequest {
  adSpaceUuid: string;
  advertiserName: string;
  advertiserEmail: string;
  startDate: string; // yyyy-MM-dd
  endDate: string;   // yyyy-MM-dd
}

export interface BookingResponseDto {
  uuid: string;
  adSpace: AdSpaceResponseDto;
  advertiserName: string;
  advertiserEmail: string;
  startDate: string;
  endDate: string;
  status: BookingStatus;
  totalCost: number;
  createdAt: string;
}
