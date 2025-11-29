// import { useEffect, useState } from "react";
// import {
//   Container,
//   Typography,
//   Box,
//   TextField,
//   Button,
//   MenuItem,
//   Table,
//   TableBody,
//   TableCell,
//   TableContainer,
//   TableHead,
//   TableRow,
//   Paper,
//   Dialog,
//   DialogTitle,
//   DialogContent,
//   DialogContentText,
//   DialogActions,
// } from "@mui/material";

// import { getAdSpaces, getAdSpace } from "./adSpacesApi";
// import {
//   AdSpaceResponseDto,
//   AdSpaceType,
//   AdSpaceAvailabilityStatus,
// } from "./types";

// function App() {
//   const [city, setCity] = useState("");
//   const [type, setType] = useState<AdSpaceType | "">("");
//   const [spaces, setSpaces] = useState<AdSpaceResponseDto[]>([]);
//   const [loading, setLoading] = useState(false);
//   const [error, setError] = useState<string | null>(null);

//   const [selectedSpace, setSelectedSpace] = useState<AdSpaceResponseDto | null>(null);
//   const [detailsLoading, setDetailsLoading] = useState(false);

//   const adSpaceTypes = Object.values(AdSpaceType);

//   const loadSpaces = async () => {
//     setLoading(true);
//     setError(null);

//     try {
//       const data = await getAdSpaces({
//         city: city || undefined,
//         type: type || undefined,
//       });
//       setSpaces(data);
//     } catch (e: any) {
//       setError(e.message);
//     } finally {
//       setLoading(false);
//     }
//   };

//   useEffect(() => {
//     loadSpaces();
//     // eslint-disable-next-line react-hooks/exhaustive-deps
//   }, []);

//   const handleRowClick = async (space: AdSpaceResponseDto) => {
//     setDetailsLoading(true);
//     try {
//       const full = await getAdSpace(space.uuid);
//       setSelectedSpace(full);
//     } catch (e: any) {
//       setError(e.message);
//     } finally {
//       setDetailsLoading(false);
//     }
//   };

//   return (
//     <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
//       <Typography variant="h4" gutterBottom>
//         Available Ad Spaces
//       </Typography>

//       {/* FILTRE */}
//       <Box
//         component={Paper}
//         sx={{
//           p: 2,
//           mb: 3,
//           display: "flex",
//           gap: 2,
//           flexWrap: "wrap",
//           alignItems: "center",
//         }}
//       >
//         <TextField
//           label="City"
//           value={city}
//           onChange={(e) => setCity(e.target.value)}
//           size="small"
//         />

//         <TextField
//           select
//           label="Type"
//           value={type}
//           onChange={(e) => setType(e.target.value as AdSpaceType)}
//           size="small"
//           sx={{ minWidth: 180 }}
//         >
//           <MenuItem value="">All</MenuItem>
//           {adSpaceTypes.map((t) => (
//             <MenuItem key={t} value={t}>
//               {t}
//             </MenuItem>
//           ))}
//         </TextField>

//         <Button variant="contained" onClick={loadSpaces} disabled={loading}>
//           {loading ? "Loading..." : "Search"}
//         </Button>

//         {error && (
//           <Typography color="error" sx={{ ml: 2 }}>
//             {error}
//           </Typography>
//         )}
//       </Box>

//       {/* TABEL */}
//       <TableContainer component={Paper}>
//         <Table>
//           <TableHead>
//             <TableRow>
//               <TableCell>Name</TableCell>
//               <TableCell>City</TableCell>
//               <TableCell>Address</TableCell>
//               <TableCell>Type</TableCell>
//               <TableCell>Price/Day</TableCell>
//               <TableCell>Status</TableCell>
//             </TableRow>
//           </TableHead>

//           <TableBody>
//             {spaces.map((space) => (
//               <TableRow
//                 key={space.uuid}
//                 hover
//                 sx={{ cursor: "pointer" }}
//                 onClick={() => handleRowClick(space)}
//               >
//                 <TableCell>{space.name}</TableCell>
//                 <TableCell>{space.city}</TableCell>
//                 <TableCell>{space.address}</TableCell>
//                 <TableCell>{space.type}</TableCell>
//                 <TableCell>{space.pricePerDay} €</TableCell>
//                 <TableCell>{space.availabilityStatus}</TableCell>
//               </TableRow>
//             ))}

//             {spaces.length === 0 && !loading && (
//               <TableRow>
//                 <TableCell colSpan={6}>
//                   <Typography>No spaces found.</Typography>
//                 </TableCell>
//               </TableRow>
//             )}
//           </TableBody>
//         </Table>
//       </TableContainer>

//       {/* DIALOG DETALII */}
//       <Dialog
//         open={!!selectedSpace}
//         onClose={() => setSelectedSpace(null)}
//         fullWidth
//         maxWidth="sm"
//       >
//         <DialogTitle>
//           {detailsLoading
//             ? "Loading..."
//             : selectedSpace?.name || "Ad Space Details"}
//         </DialogTitle>

//         <DialogContent dividers>
//           {detailsLoading && (
//             <DialogContentText>Loading details…</DialogContentText>
//           )}

//           {!detailsLoading && selectedSpace && (
//             <>
//               <DialogContentText>
//                 <strong>City:</strong> {selectedSpace.city}
//               </DialogContentText>
//               <DialogContentText>
//                 <strong>Address:</strong> {selectedSpace.address}
//               </DialogContentText>
//               <DialogContentText>
//                 <strong>Type:</strong> {selectedSpace.type}
//               </DialogContentText>
//               <DialogContentText>
//                 <strong>Price/Day:</strong> {selectedSpace.pricePerDay} €
//               </DialogContentText>
//               <DialogContentText>
//                 <strong>Status:</strong> {selectedSpace.availabilityStatus}
//               </DialogContentText>
//             </>
//           )}
//         </DialogContent>

//         <DialogActions>
//           <Button onClick={() => setSelectedSpace(null)}>Close</Button>
//         </DialogActions>
//       </Dialog>
//     </Container>
//   );
// }

// export default App;

// import { Container, Box } from "@mui/material";
// import AdSpaceList from "./components/AdSpaceList";

// function App() {
//   return (
//     <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
//       <Box>
//         <AdSpaceList />
//       </Box>
//     </Container>
//   );
// }

// export default App;

// src/App.tsx
import { Container, CssBaseline, Box, Typography } from "@mui/material";
import AdSpaceList from "./components/AdSpaceList";
import BookingList from "./components/BookingList";

function App() {
  return (
    <>
      <CssBaseline />
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Typography variant="h3" gutterBottom>
          Ad Space Booking System
        </Typography>

        <Box display="flex" flexDirection="column" gap={4}>
          <AdSpaceList />
          <BookingList />
        </Box>
      </Container>
    </>
  );
}

export default App;

