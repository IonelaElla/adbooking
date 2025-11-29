import { useEffect, useState } from "react";
import {
  Box,
  Card,
  CardContent,
  Typography,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Button,
  Table,
  TableHead,
  TableRow,
  TableCell,
  TableBody,
  TableContainer,
  Paper,
  Alert,
} from "@mui/material";
import { BookingStatus } from "../types";
import { useBookingStore } from "../store/useBookingStore";

const BOOKING_STATUSES: (BookingStatus | "ALL")[] = [
  "ALL",
  "PENDING",
  "APPROVED",
  "REJECTED",
];

const BookingList = () => {
  const {
    bookings,
    loading,
    error,
    filterStatus,
    setFilterStatus,
    fetchBookings,
    updateStatus,
  } = useBookingStore();

  useEffect(() => {
    fetchBookings();
  }, [fetchBookings, filterStatus]);

  const handleStatusChange = (event: any) => {
    setFilterStatus(event.target.value as BookingStatus | "ALL");
  };
  const [actionError, setActionError] = useState<string | null>(null);

  return (
    <Card>
      <CardContent>
        <Typography variant="h4" gutterBottom>
          Booking Requests
        </Typography>

        {actionError && (
          <Box mb={2}>
            <Alert severity="error">{actionError}</Alert>
          </Box>
        )}


        <Box
          sx={{
            display: "flex",
            gap: 2,
            mb: 2,
            flexWrap: "wrap",
            alignItems: "center",
          }}
        >
          <FormControl size="small" sx={{ minWidth: 200 }}>
            <InputLabel id="status-label">Status</InputLabel>
            <Select
              labelId="status-label"
              label="Status"
              value={filterStatus}
              onChange={handleStatusChange}
            >
              {BOOKING_STATUSES.map((status) => (
                <MenuItem key={status} value={status}>
                  {status}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          {error && (
            <Typography color="error" sx={{ flex: 1 }}>
              {error}
            </Typography>
          )}
        </Box>

        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Ad Space</TableCell>
                <TableCell>Advertiser</TableCell>
                <TableCell>Dates</TableCell>
                <TableCell>Status</TableCell>
                <TableCell>Total Cost</TableCell>
                <TableCell align="right">Actions</TableCell>
              </TableRow>
            </TableHead>

            <TableBody>
              {bookings.map((b) => {
                if (!b) return null;

                const adSpaceName =
                  (b as any).adSpace?.name ??
                  (b as any).adSpaceName ??
                  "Unknown";

                return (
                  <TableRow key={b.uuid} hover>
                    <TableCell>{adSpaceName}</TableCell>

                    <TableCell>
                      {b.advertiserName}
                      <br />
                      <Typography variant="caption" color="text.secondary">
                        {b.advertiserEmail}
                      </Typography>
                    </TableCell>

                    <TableCell>
                      {b.startDate} → {b.endDate}
                    </TableCell>

                    <TableCell>{b.status}</TableCell>

                    <TableCell>{b.totalCost} €</TableCell>

                    <TableCell align="right">
                      {b.status === "PENDING" && (
                        <Box display="flex" gap={1} justifyContent="flex-end">
                          <Button
                            size="small"
                            variant="contained"
                            onClick={async () => {
                              setActionError(null);
                              try {
                                await updateStatus(b.uuid, "approve");
                              } catch (e: any) {
                                setActionError(e.message);
                              }
                            }}
                            disabled={loading}
                          >
                            Approve
                          </Button>

                          <Button
                            size="small"
                            variant="outlined"
                            color="error"
                            onClick={async () => {
                              setActionError(null);
                              try {
                                await updateStatus(b.uuid, "reject");
                              } catch (e: any) {
                                setActionError(e.message);
                              }
                            }}
                            disabled={loading}
                          >
                            Reject
                          </Button>

                        </Box>
                      )}
                    </TableCell>
                  </TableRow>
                );
              })}

              {!loading && bookings.length === 0 && (
                <TableRow>
                  <TableCell colSpan={6}>
                    <Typography>No bookings found.</Typography>
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </CardContent>
    </Card>
  );
};

export default BookingList;
