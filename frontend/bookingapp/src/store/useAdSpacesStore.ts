import { create } from "zustand";
import { AdSpaceResponseDto, AdSpaceType } from "../types";
import { getAdSpaces } from "../api/adSpacesApi";

type Filters = {
  city: string;
  type: AdSpaceType | "";
};

interface AdSpacesState {
  spaces: AdSpaceResponseDto[];
  loading: boolean;
  error: string | null;
  filters: Filters;
  selectedSpace: AdSpaceResponseDto | null;

  setFilters: (patch: Partial<Filters>) => void;
  fetchSpaces: () => Promise<void>;
  setSelectedSpace: (space: AdSpaceResponseDto | null) => void;
  markSpaceAsUnavailable: (uuid: string) => void; // kept for compatibility, now no-op
}

export const useAdSpacesStore = create<AdSpacesState>((set, get) => ({
  spaces: [],
  loading: false,
  error: null,
  filters: { city: "", type: "" },
  selectedSpace: null,

  setFilters: (patch) =>
    set((state) => ({ filters: { ...state.filters, ...patch } })),

  fetchSpaces: async () => {
    const { filters } = get();
    set({ loading: true, error: null });
    try {
      const spaces = await getAdSpaces({
        city: filters.city || undefined,
        type: filters.type || undefined,
      });
      set({ spaces });
    } catch (e: any) {
      set({
        error: e?.message || "Failed to fetch ad spaces",
        spaces: [],
      });
    } finally {
      set({ loading: false });
    }
  },

  setSelectedSpace: (space) => set({ selectedSpace: space }),

  markSpaceAsUnavailable: (_uuid: string) => {},
}));
