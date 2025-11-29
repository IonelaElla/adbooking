import { useEffect, useState } from "react";
import {
  Box,
  Button,
  Card,
  CardContent,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from "@mui/material";
import { AD_SPACE_TYPES, AdSpaceType } from "../types";
import { useAdSpacesStore } from "../store/useAdSpacesStore";
import BookingRequestForm from "./BookingRequestForm";

const CITY_OPTIONS = ["Bucharest", "Oradea"];

const AdSpaceList = () => {
  const {
    spaces,
    loading,
    error,
    filters,
    setFilters,
    fetchSpaces,
    setSelectedSpace,
  } = useAdSpacesStore();

  const [bookingOpen, setBookingOpen] = useState(false);

  useEffect(() => {
    fetchSpaces();
  }, [fetchSpaces]);

  const handleBookClick = (space: any) => {
    setSelectedSpace(space);
    setBookingOpen(true);
  };

  const handleCloseBooking = () => {
    setBookingOpen(false);
    setSelectedSpace(null);
  };

  // helper to read status from whatever the backend sends
  const getStatus = (space: any): string => {
    const raw =
      space.status ??
      space.availabilityStatus ?? // common BE name
      "";
    return String(raw);
  };

  const isAvailable = (space: any): boolean => {
    const status = getStatus(space).toUpperCase();
    return status === "AVAILABLE";
  };

  return (
    <Card>
      <CardContent>
        <Typography variant="h4" gutterBottom>
          Available Ad Spaces
        </Typography>

        {/* Filters with Box, no Grid */}
        <Box
          sx={{
            display: "grid",
            gridTemplateColumns: {
              xs: "1fr",
              sm: "repeat(3, minmax(0, 1fr))",
            },
            gap: 2,
            mb: 3,
            alignItems: "center",
          }}
        >
          <Box>
            <FormControl fullWidth size="small">
              <InputLabel id="city-label">City</InputLabel>
              <Select
                labelId="city-label"
                label="City"
                value={filters.city}
                onChange={(e) => setFilters({ city: e.target.value })}
              >
                <MenuItem value="">All</MenuItem>
                {CITY_OPTIONS.map((city) => (
                  <MenuItem key={city} value={city}>
                    {city}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Box>

          <Box>
            <FormControl fullWidth size="small">
              <InputLabel id="type-label">Type</InputLabel>
              <Select
                labelId="type-label"
                label="Type"
                value={filters.type}
                onChange={(e) =>
                  setFilters({ type: e.target.value as AdSpaceType | "" })
                }
              >
                <MenuItem value="">All</MenuItem>
                {AD_SPACE_TYPES.map((t) => (
                  <MenuItem key={t} value={t}>
                    {t}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Box>

          <Box>
            <Button
              fullWidth
              variant="contained"
              onClick={fetchSpaces}
              disabled={loading}
              sx={{ height: "40px" }}
            >
              {loading ? "Loading..." : "SEARCH"}
            </Button>
          </Box>

          {error && (
            <Box sx={{ gridColumn: "1 / -1" }}>
              <Typography color="error">{error}</Typography>
            </Box>
          )}
        </Box>

        {/* Table list */}
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Name</TableCell>
                <TableCell>Type</TableCell>
                <TableCell>City</TableCell>
                <TableCell>Price/Day</TableCell>
                <TableCell>Status</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>

            <TableBody>
              {spaces.map((space) => (
                <TableRow key={space.uuid} hover>
                  <TableCell>{space.name}</TableCell>
                  <TableCell>{space.type}</TableCell>
                  <TableCell>{space.city}</TableCell>
                  <TableCell>{space.pricePerDay} €</TableCell>

                  {/* show whatever status the backend gives */}
                  <TableCell>{getStatus(space)}</TableCell>

                  <TableCell align="right">
                    <Box display="flex" gap={1} justifyContent="flex-end">
                      <Button
                        size="small"
                        variant="contained"
                        // 🔑 only disabled if definitely not AVAILABLE
                        disabled={!isAvailable(space)}
                        onClick={() => handleBookClick(space)}
                      >
                        Book Now
                      </Button>

                      <Button size="small" variant="outlined">
                        Edit
                      </Button>

                      <Button
                        size="small"
                        color="error"
                        variant="outlined"
                      >
                        Delete
                      </Button>
                    </Box>
                  </TableCell>
                </TableRow>
              ))}

              {!loading && spaces.length === 0 && (
                <TableRow>
                  <TableCell colSpan={6}>
                    <Typography>No spaces found.</Typography>
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>

        <BookingRequestForm open={bookingOpen} onClose={handleCloseBooking} />
      </CardContent>
    </Card>
  );
};

export default AdSpaceList;
