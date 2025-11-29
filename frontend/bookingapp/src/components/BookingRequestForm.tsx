import { useState, useMemo } from "react";
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Typography,
  Alert,
} from "@mui/material";
import dayjs from "dayjs";
import { useAdSpacesStore } from "../store/useAdSpacesStore";
import { useBookingStore } from "../store/useBookingStore";

interface BookingRequestFormProps {
  open: boolean;
  onClose: () => void;
}

interface FormErrors {
  advertiserName?: string;
  advertiserEmail?: string;
  startDate?: string;
  endDate?: string;
  general?: string;
}

interface Touched {
  advertiserName: boolean;
  advertiserEmail: boolean;
  startDate: boolean;
  endDate: boolean;
}

const MIN_DAYS = 7;

const BookingRequestForm = ({ open, onClose }: BookingRequestFormProps) => {
  const selectedSpace = useAdSpacesStore((s) => s.selectedSpace);
  const createBooking = useBookingStore((s) => s.createBooking);

  const [advertiserName, setAdvertiserName] = useState("");
  const [advertiserEmail, setAdvertiserEmail] = useState("");
  const [startDate, setStartDate] = useState(""); // yyyy-MM-dd
  const [endDate, setEndDate] = useState("");

  const [submitting, setSubmitting] = useState(false);
  const [errors, setErrors] = useState<FormErrors>({});
  const [touched, setTouched] = useState<Touched>({
    advertiserName: false,
    advertiserEmail: false,
    startDate: false,
    endDate: false,
  });
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const bookingDays = useMemo(() => {
    if (!startDate || !endDate) return null;
    const start = dayjs(startDate);
    const end = dayjs(endDate);
    if (!start.isValid() || !end.isValid()) return null;
    const days = end.diff(start, "day"); // same as Period.between
    if (days <= 0) return null;
    return days;
  }, [startDate, endDate]);

  const totalCost = useMemo(() => {
    if (!selectedSpace) return null;
    if (bookingDays == null) return null;
    return bookingDays * selectedSpace.pricePerDay;
  }, [bookingDays, selectedSpace]);

  const resetForm = () => {
    setAdvertiserName("");
    setAdvertiserEmail("");
    setStartDate("");
    setEndDate("");
    setErrors({});
    setSuccessMessage(null);
    setTouched({
      advertiserName: false,
      advertiserEmail: false,
      startDate: false,
      endDate: false,
    });
  };

  const handleClose = () => {
    if (submitting) return;
    resetForm();
    onClose();
  };

  const runValidation = (
    override?: Partial<{
      advertiserName: string;
      advertiserEmail: string;
      startDate: string;
      endDate: string;
    }>
  ): FormErrors => {
    const name = override?.advertiserName ?? advertiserName;
    const email = override?.advertiserEmail ?? advertiserEmail;
    const startVal = override?.startDate ?? startDate;
    const endVal = override?.endDate ?? endDate;

    const newErrors: FormErrors = {};

    if (!name.trim()) {
      newErrors.advertiserName = "Name is required";
    }

    if (!email.trim()) {
      newErrors.advertiserEmail = "Email is required";
    } else if (!/^\S+@\S+\.\S+$/.test(email)) {
      newErrors.advertiserEmail = "Invalid email format";
    }

    const today = dayjs().startOf("day");
    const start = dayjs(startVal);
    const end = dayjs(endVal);

    if (!startVal) {
      newErrors.startDate = "Start date is required";
    } else if (!start.isValid()) {
      newErrors.startDate = "Invalid start date";
    } else {
      const tomorrow = today.add(1, "day");
      if (!start.isAfter(today)) {
        newErrors.startDate = "Start date must be in the future (from tomorrow)";
      } else if (start.isBefore(tomorrow)) {
        newErrors.startDate = "Start date must be at least tomorrow";
      }
    }

    if (!endVal) {
      newErrors.endDate = "End date is required";
    } else if (!end.isValid()) {
      newErrors.endDate = "Invalid end date";
    } else if (!startVal || !start.isValid()) {
      // start already invalid
    } else {
      const days = end.diff(start, "day");
      if (days <= 0) {
        newErrors.endDate = "End date must be after start date";
      } else if (days < MIN_DAYS) {
        newErrors.endDate =
          "End date must be after start date. Minimum booking duration: 7 days";
      }
    }

    setErrors(newErrors);
    return newErrors;
  };

  const isFormValid = () => {
    const currentErrors = runValidation();
    return Object.keys(currentErrors).length === 0;
  };

  const handleNameChange = (value: string) => {
    setAdvertiserName(value);
    setTouched((t) => ({ ...t, advertiserName: true }));
    runValidation({ advertiserName: value });
  };

  const handleEmailChange = (value: string) => {
    setAdvertiserEmail(value);
    setTouched((t) => ({ ...t, advertiserEmail: true }));
    runValidation({ advertiserEmail: value });
  };

  const handleStartDateChange = (value: string) => {
    setStartDate(value);
    setTouched((t) => ({ ...t, startDate: true }));
    runValidation({ startDate: value });
  };

  const handleEndDateChange = (value: string) => {
    setEndDate(value);
    setTouched((t) => ({ ...t, endDate: true }));
    runValidation({ endDate: value });
  };

  const fieldColor = (field: keyof Touched, errorKey: keyof FormErrors) => {
    if (errors[errorKey]) return "error" as const;
    if (touched[field] && !errors[errorKey]) return "success" as const;
    return undefined;
  };

  const handleSubmit = async () => {
    if (!selectedSpace) return;
    if (!isFormValid()) return;

    setSubmitting(true);
    setSuccessMessage(null);

    try {
      const booking = await createBooking({
        adSpaceUuid: selectedSpace.uuid,
        advertiserName,
        advertiserEmail,
        startDate,
        endDate,
      });

      // ✅ no local status change; trust backend
      setSuccessMessage(`Booking created with status: ${booking.status}`);
    } catch (e: any) {
      setErrors((prev) => ({
        ...prev,
        general: e?.message || "Failed to create booking",
      }));
    } finally {
      setSubmitting(false);
    }
  };

  if (!selectedSpace) {
    return null;
  }

  return (
    <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
      <DialogTitle>Book: {selectedSpace.name}</DialogTitle>
      <DialogContent>
        <Box mt={1} mb={2}>
          <Typography variant="subtitle2" color="text.secondary">
            {selectedSpace.type} · {selectedSpace.city}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Price per day: {selectedSpace.pricePerDay} €
          </Typography>
        </Box>

        {errors.general && (
          <Box mb={2}>
            <Alert severity="error">{errors.general}</Alert>
          </Box>
        )}

        {successMessage && (
          <Box mb={2}>
            <Alert severity="success">{successMessage}</Alert>
          </Box>
        )}

        <Box display="flex" flexDirection="column" gap={2} mt={1}>
          <TextField
            label="Advertiser Name"
            value={advertiserName}
            onChange={(e) => handleNameChange(e.target.value)}
            onBlur={() =>
              setTouched((t) => ({ ...t, advertiserName: true }))
            }
            error={!!errors.advertiserName}
            helperText={errors.advertiserName || " "}
            fullWidth
            color={fieldColor("advertiserName", "advertiserName")}
          />

          <TextField
            label="Advertiser Email"
            value={advertiserEmail}
            onChange={(e) => handleEmailChange(e.target.value)}
            onBlur={() =>
              setTouched((t) => ({ ...t, advertiserEmail: true }))
            }
            error={!!errors.advertiserEmail}
            helperText={errors.advertiserEmail || " "}
            fullWidth
            color={fieldColor("advertiserEmail", "advertiserEmail")}
          />

          <Box display="flex" gap={2}>
            <TextField
              label="Start Date"
              type="date"
              value={startDate}
              onChange={(e) => handleStartDateChange(e.target.value)}
              onBlur={() =>
                setTouched((t) => ({ ...t, startDate: true }))
              }
              InputLabelProps={{ shrink: true }}
              error={!!errors.startDate}
              helperText={errors.startDate || " "}
              fullWidth
              color={fieldColor("startDate", "startDate")}
            />
            <TextField
              label="End Date"
              type="date"
              value={endDate}
              onChange={(e) => handleEndDateChange(e.target.value)}
              onBlur={() => setTouched((t) => ({ ...t, endDate: true }))}
              InputLabelProps={{ shrink: true }}
              error={!!errors.endDate}
              helperText={errors.endDate || " "}
              fullWidth
              color={fieldColor("endDate", "endDate")}
            />
          </Box>

          <Box mt={1}>
            <Typography variant="subtitle1">
              Total cost:{" "}
              {totalCost != null ? `${totalCost.toFixed(2)} €` : "-"}
            </Typography>
          </Box>
        </Box>
      </DialogContent>

      <DialogActions>
        <Button onClick={handleClose} disabled={submitting}>
          Cancel
        </Button>
        <Button
          variant="contained"
          onClick={handleSubmit}
          disabled={submitting}
        >
          {submitting ? "Submitting..." : "Create Booking"}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default BookingRequestForm;
