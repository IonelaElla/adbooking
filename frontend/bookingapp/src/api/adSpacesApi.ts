import { AdSpaceResponseDto, AdSpaceType } from "../types";

const API_BASE_URL =
  process.env.REACT_APP_API_BASE_URL ?? "http://localhost:8080";

export interface GetAdSpacesParams {
  type?: AdSpaceType;
  city?: string;
}

export async function getAdSpaces(
  params: GetAdSpacesParams = {}
): Promise<AdSpaceResponseDto[]> {
  const searchParams = new URLSearchParams();
  if (params.type) searchParams.append("type", params.type);
  if (params.city) searchParams.append("city", params.city);

  const query = searchParams.toString();
  const url = `${API_BASE_URL}/api/v1/ad-spaces${query ? `?${query}` : ""}`;

  const res = await fetch(url);
  if (!res.ok) {
    throw new Error(`Failed to fetch ad spaces (${res.status})`);
  }
  return res.json();
}

export async function getAdSpaceById(
  id: string
): Promise<AdSpaceResponseDto> {
  const res = await fetch(`${API_BASE_URL}/api/v1/ad-spaces/${id}`);
  if (!res.ok) {
    throw new Error("Ad space not found");
  }
  return res.json();
}
