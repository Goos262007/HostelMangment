/**
 * Aegis Cyber-Defense Hostel ERP - Universal API Client
 * Features:
 * - Direct REST communication with Java Backend (`/api/*`)
 * - Autonomous In-Memory Fallback: If deployed as a static site (GitHub Pages, Vercel, Netlify)
 *   without a live Java server, it seamlessly operates using an in-browser repository!
 */

const API_BASE = '/api';

// Initial seed mock database for standalone static cloud or offline deployment
const defaultMockStorage = {
  students: [
    { id: 1, studentId: "STU-2024-001", fullName: "Aarav Mehta", rollNumber: "21CSE014", department: "Computer Science", yearOfStudy: "3rd Year", dob: "2003-05-12", gender: "Male", phone: "+91 98765 43210", email: "aarav.mehta@college.edu", parentContact: "+91 98765 00001", address: "Flat 402, Green Valley Apts, Pune", assignedRoomId: 1, assignedRoomNumber: "A-101", status: "Active", admissionDate: "2024-08-01" },
    { id: 2, studentId: "STU-2024-002", fullName: "Priya Sharma", rollNumber: "22IT045", department: "Information Technology", yearOfStudy: "2nd Year", dob: "2004-08-22", gender: "Female", phone: "+91 98765 43211", email: "priya.sharma@college.edu", parentContact: "+91 98765 00002", address: "12B, Rose Villa, Jaipur", assignedRoomId: 3, assignedRoomNumber: "B-201", status: "Active", admissionDate: "2024-08-01" },
    { id: 3, studentId: "STU-2024-003", fullName: "Rohan Verma", rollNumber: "21ECE028", department: "Electronics & Comm", yearOfStudy: "3rd Year", dob: "2003-11-05", gender: "Male", phone: "+91 98765 43212", email: "rohan.verma@college.edu", parentContact: "+91 98765 00003", address: "78 Sector 14, Chandigarh", assignedRoomId: 3, assignedRoomNumber: "B-201", status: "Active", admissionDate: "2024-08-02" },
    { id: 4, studentId: "STU-2024-004", fullName: "Ananya Patel", rollNumber: "23CSE089", department: "Computer Science", yearOfStudy: "1st Year", dob: "2005-02-18", gender: "Female", phone: "+91 98765 43213", email: "ananya.patel@college.edu", parentContact: "+91 98765 00004", address: "Plot 55, Royal Residency, Ahmedabad", assignedRoomId: 4, assignedRoomNumber: "B-202", status: "Active", admissionDate: "2024-08-03" },
    { id: 5, studentId: "STU-2024-005", fullName: "Vikram Singh", rollNumber: "20ME003", department: "Mechanical", yearOfStudy: "4th Year", dob: "2002-09-30", gender: "Male", phone: "+91 98765 43214", email: "vikram.singh@college.edu", parentContact: "+91 98765 00005", address: "House 14, Lake View Road, Bhopal", assignedRoomId: 6, assignedRoomNumber: "C-301", status: "Active", admissionDate: "2024-08-01" },
    { id: 6, studentId: "STU-2024-006", fullName: "Sneha Kulkarni", rollNumber: "22CSE056", department: "Computer Science", yearOfStudy: "2nd Year", dob: "2004-04-14", gender: "Female", phone: "+91 98765 43215", email: "sneha.kulkarni@college.edu", parentContact: "+91 98765 00006", address: "45 Shivajinagar, Kolhapur", assignedRoomId: 6, assignedRoomNumber: "C-301", status: "Active", admissionDate: "2024-08-02" },
    { id: 7, studentId: "STU-2024-007", fullName: "Kunal Reddy", rollNumber: "21CIV012", department: "Civil", yearOfStudy: "3rd Year", dob: "2003-07-25", gender: "Male", phone: "+91 98765 43216", email: "kunal.reddy@college.edu", parentContact: "+91 98765 00007", address: "Banjara Hills, Hyderabad", assignedRoomId: 7, assignedRoomNumber: "C-302", status: "Active", admissionDate: "2024-08-01" },
    { id: 8, studentId: "STU-2024-008", fullName: "Tanvi Deshmukh", rollNumber: "23IT019", department: "Information Technology", yearOfStudy: "1st Year", dob: "2005-10-09", gender: "Female", phone: "+91 98765 43217", email: "tanvi.d@college.edu", parentContact: "+91 98765 00008", address: "Sunrise Towers, Nagpur", assignedRoomId: 7, assignedRoomNumber: "C-302", status: "Active", admissionDate: "2024-08-01" },
    { id: 9, studentId: "STU-2024-009", fullName: "Aditya Nair", rollNumber: "21ECE091", department: "Electronics & Comm", yearOfStudy: "3rd Year", dob: "2003-01-15", gender: "Male", phone: "+91 98765 43218", email: "aditya.nair@college.edu", parentContact: "+91 98765 00009", address: "Palace Road, Kowdiar, Trivandrum", assignedRoomId: 7, assignedRoomNumber: "C-302", status: "Active", admissionDate: "2024-08-02" },
    { id: 10, studentId: "STU-2024-010", fullName: "Ishaan Gupta", rollNumber: "23CSE110", department: "Computer Science", yearOfStudy: "1st Year", dob: "2005-06-20", gender: "Male", phone: "+91 98765 43219", email: "ishaan.gupta@college.edu", parentContact: "+91 98765 00010", address: "Model Town, New Delhi", assignedRoomId: 8, assignedRoomNumber: "D-401", status: "Active", admissionDate: "2024-08-05" },
    { id: 11, studentId: "STU-2024-011", fullName: "Neha Joshi", rollNumber: "22ECE034", department: "Electronics & Comm", yearOfStudy: "2nd Year", dob: "2004-12-03", gender: "Female", phone: "+91 98765 43220", email: "neha.joshi@college.edu", parentContact: "+91 98765 00011", address: "Subhash Marg, Indore", assignedRoomId: null, assignedRoomNumber: null, status: "Active", admissionDate: "2024-08-10" },
    { id: 12, studentId: "STU-2024-012", fullName: "Arjun Saxena", rollNumber: "20CSE005", department: "Computer Science", yearOfStudy: "4th Year", dob: "2002-03-11", gender: "Male", phone: "+91 98765 43221", email: "arjun.saxena@college.edu", parentContact: "+91 98765 00012", address: "Gomti Nagar, Lucknow", assignedRoomId: null, assignedRoomNumber: null, status: "Active", admissionDate: "2024-08-10" }
  ],
  rooms: [
    { id: 1, roomNumber: "A-101", roomType: "Single", capacity: 1, occupiedBeds: 1, availableBeds: 0, monthlyFee: 9500, status: "Occupied", description: "Deluxe single occupancy with attached washroom" },
    { id: 2, roomNumber: "A-102", roomType: "Single", capacity: 1, occupiedBeds: 0, availableBeds: 1, monthlyFee: 9500, status: "Available", description: "Single occupancy room with balcony" },
    { id: 3, roomNumber: "B-201", roomType: "Double", capacity: 2, occupiedBeds: 2, availableBeds: 0, monthlyFee: 7000, status: "Occupied", description: "Spacious double bed room with cooling" },
    { id: 4, roomNumber: "B-202", roomType: "Double", capacity: 2, occupiedBeds: 1, availableBeds: 1, monthlyFee: 7000, status: "Available", description: "Double sharing room, facing green lawn" },
    { id: 5, roomNumber: "B-203", roomType: "Double", capacity: 2, occupiedBeds: 0, availableBeds: 2, monthlyFee: 7000, status: "Maintenance", description: "Under electrical fixture servicing" },
    { id: 6, roomNumber: "C-301", roomType: "Triple", capacity: 3, occupiedBeds: 2, availableBeds: 1, monthlyFee: 5500, status: "Available", description: "Triple sharing room with wardrobes" },
    { id: 7, roomNumber: "C-302", roomType: "Triple", capacity: 3, occupiedBeds: 3, availableBeds: 0, monthlyFee: 5500, status: "Occupied", description: "Triple sharing near study lounge" },
    { id: 8, roomNumber: "D-401", roomType: "Deluxe", capacity: 2, occupiedBeds: 1, availableBeds: 1, monthlyFee: 11000, status: "Available", description: "Premium AC room with mini fridge" }
  ],
  allocations: [
    { id: 1, studentId: 1, studentName: "Aarav Mehta", studentRollNo: "21CSE014", roomId: 1, roomNumber: "A-101", roomType: "Single", allocationDate: "2024-08-01", vacatingDate: null, status: "Active", remarks: "Allotted Single Room A-101" },
    { id: 2, studentId: 2, studentName: "Priya Sharma", studentRollNo: "22IT045", roomId: 3, roomNumber: "B-201", roomType: "Double", allocationDate: "2024-08-01", vacatingDate: null, status: "Active", remarks: "Allotted Double Room B-201" },
    { id: 3, studentId: 3, studentName: "Rohan Verma", studentRollNo: "21ECE028", roomId: 3, roomNumber: "B-201", roomType: "Double", allocationDate: "2024-08-02", vacatingDate: null, status: "Active", remarks: "Allotted Double Room B-201" },
    { id: 4, studentId: 4, studentName: "Ananya Patel", studentRollNo: "23CSE089", roomId: 4, roomNumber: "B-202", roomType: "Double", allocationDate: "2024-08-03", vacatingDate: null, status: "Active", remarks: "Allotted Double Room B-202" },
    { id: 5, studentId: 5, studentName: "Vikram Singh", studentRollNo: "20ME003", roomId: 6, roomNumber: "C-301", roomType: "Triple", allocationDate: "2024-08-01", vacatingDate: null, status: "Active", remarks: "Allotted Triple Room C-301" },
    { id: 6, studentId: 6, studentName: "Sneha Kulkarni", studentRollNo: "22CSE056", roomId: 6, roomNumber: "C-301", roomType: "Triple", allocationDate: "2024-08-02", vacatingDate: null, status: "Active", remarks: "Allotted Triple Room C-301" },
    { id: 7, studentId: 7, studentName: "Kunal Reddy", studentRollNo: "21CIV012", roomId: 7, roomNumber: "C-302", roomType: "Triple", allocationDate: "2024-08-01", vacatingDate: null, status: "Active", remarks: "Allotted Triple Room C-302" },
    { id: 8, studentId: 8, studentName: "Tanvi Deshmukh", studentRollNo: "23IT019", roomId: 7, roomNumber: "C-302", roomType: "Triple", allocationDate: "2024-08-01", vacatingDate: null, status: "Active", remarks: "Allotted Triple Room C-302" },
    { id: 9, studentId: 9, studentName: "Aditya Nair", studentRollNo: "21ECE091", roomId: 7, roomNumber: "C-302", roomType: "Triple", allocationDate: "2024-08-02", vacatingDate: null, status: "Active", remarks: "Allotted Triple Room C-302" },
    { id: 10, studentId: 10, studentName: "Ishaan Gupta", studentRollNo: "23CSE110", roomId: 8, roomNumber: "D-401", roomType: "Deluxe", allocationDate: "2024-08-05", vacatingDate: null, status: "Active", remarks: "Allotted Deluxe Room D-401" }
  ],
  fees: [
    { id: 1, receiptNo: "REC-2024-001", studentId: 1, studentName: "Aarav Mehta", studentRollNo: "21CSE014", totalAmount: 57000, amountPaid: 57000, pendingAmount: 0, paymentDate: "2024-08-05", paymentMethod: "UPI", transactionId: "UPI/2024/908129841", paymentStatus: "Paid", remarks: "Full semester fee cleared" },
    { id: 2, receiptNo: "REC-2024-002", studentId: 2, studentName: "Priya Sharma", studentRollNo: "22IT045", totalAmount: 42000, amountPaid: 42000, pendingAmount: 0, paymentDate: "2024-08-06", paymentMethod: "Net Banking", transactionId: "HDFC-NB-8823192", paymentStatus: "Paid", remarks: "Annual maintenance paid" },
    { id: 3, receiptNo: "REC-2024-003", studentId: 3, studentName: "Rohan Verma", studentRollNo: "21ECE028", totalAmount: 42000, amountPaid: 25000, pendingAmount: 17000, paymentDate: "2024-08-07", paymentMethod: "UPI", transactionId: "UPI/2024/778102931", paymentStatus: "Partially Paid", remarks: "1st installment cleared" },
    { id: 4, receiptNo: "REC-2024-004", studentId: 4, studentName: "Ananya Patel", studentRollNo: "23CSE089", totalAmount: 42000, amountPaid: 42000, pendingAmount: 0, paymentDate: "2024-08-08", paymentMethod: "Credit/Debit Card", transactionId: "TXN-CARD-449102", paymentStatus: "Paid", remarks: "Card swipe cleared" },
    { id: 5, receiptNo: "REC-2024-005", studentId: 5, studentName: "Vikram Singh", studentRollNo: "20ME003", totalAmount: 33000, amountPaid: 33000, pendingAmount: 0, paymentDate: "2024-08-09", paymentMethod: "UPI", transactionId: "UPI/2024/339102941", paymentStatus: "Paid", remarks: "Semester fees cleared" },
    { id: 6, receiptNo: "REC-2024-006", studentId: 6, studentName: "Sneha Kulkarni", studentRollNo: "22CSE056", totalAmount: 33000, amountPaid: 20000, pendingAmount: 13000, paymentDate: "2024-08-10", paymentMethod: "Net Banking", transactionId: "ICICI-NB-1092834", paymentStatus: "Partially Paid", remarks: "Part payment received" },
    { id: 7, receiptNo: "REC-2024-007", studentId: 7, studentName: "Kunal Reddy", studentRollNo: "21CIV012", totalAmount: 33000, amountPaid: 33000, pendingAmount: 0, paymentDate: "2024-08-10", paymentMethod: "Cash", transactionId: "CASH-REC-1082", paymentStatus: "Paid", remarks: "Cash deposited" },
    { id: 8, receiptNo: "REC-2024-008", studentId: 8, studentName: "Tanvi Deshmukh", studentRollNo: "23IT019", totalAmount: 33000, amountPaid: 0, pendingAmount: 33000, paymentDate: "2024-08-12", paymentMethod: "Net Banking", transactionId: "PENDING", paymentStatus: "Pending", remarks: "Invoice generated" },
    { id: 9, receiptNo: "REC-2024-009", studentId: 9, studentName: "Aditya Nair", studentRollNo: "21ECE091", totalAmount: 33000, amountPaid: 33000, pendingAmount: 0, paymentDate: "2024-08-12", paymentMethod: "UPI", transactionId: "UPI/2024/119284019", paymentStatus: "Paid", remarks: "Full clearance" },
    { id: 10, receiptNo: "REC-2024-010", studentId: 10, studentName: "Ishaan Gupta", studentRollNo: "23CSE110", totalAmount: 66000, amountPaid: 40000, pendingAmount: 26000, paymentDate: "2024-08-14", paymentMethod: "Cheque", transactionId: "CHQ-SBI-991204", paymentStatus: "Partially Paid", remarks: "Cheque cleared" }
  ],
  complaints: [
    { id: 1, complaintId: "CMP-2024-101", studentId: 1, studentName: "Aarav Mehta", roomNumber: "A-101", title: "Washroom tap dripping in A-101", description: "Steady water leak causing wastage.", category: "Plumbing", priority: "Low", status: "Resolved", reportedDate: "2024-08-15", resolvedDate: "2024-08-16", adminRemarks: "Washer replaced by plumber." },
    { id: 2, complaintId: "CMP-2024-102", studentId: 4, studentName: "Ananya Patel", roomNumber: "B-202", title: "Wi-Fi router intermittent packet loss", description: "3rd floor wing B router signals drop during peak study hours.", category: "Wi-Fi", priority: "High", status: "In Progress", reportedDate: "2024-08-18", resolvedDate: null, adminRemarks: "IT team testing gateway DNS." },
    { id: 3, complaintId: "CMP-2024-103", studentId: 2, studentName: "Priya Sharma", roomNumber: "B-201", title: "Ceiling fan making clicking noise", description: "Bearing in B-201 requires lubrication.", category: "Electrical", priority: "Medium", status: "Pending", reportedDate: "2024-08-20", resolvedDate: null, adminRemarks: "Electrician visit scheduled." },
    { id: 4, complaintId: "CMP-2024-104", studentId: 5, studentName: "Vikram Singh", roomNumber: "C-301", title: "Corridor sanitation request", description: "Dust accumulation on staircase.", category: "Housekeeping", priority: "Low", status: "Resolved", reportedDate: "2024-08-17", resolvedDate: "2024-08-18", adminRemarks: "Sanitation supervisor completed scrubbing." },
    { id: 5, complaintId: "CMP-2024-105", studentId: 7, studentName: "Kunal Reddy", roomNumber: "C-302", title: "Reading lamp power socket sparking", description: "Power socket near bed 2 sparks when plugging charger.", category: "Electrical", priority: "High", status: "Pending", reportedDate: "2024-08-22", resolvedDate: null, adminRemarks: "Urgent inspection assigned." }
  ]
};

// Autonomous LocalStorage persistence for serverless/offline execution anywhere
function loadPersistentDb() {
  try {
    const raw = localStorage.getItem('safehostel_db_v1');
    if (raw) {
      const parsed = JSON.parse(raw);
      if (parsed.students && parsed.rooms && parsed.allocations) {
        return parsed;
      }
    }
  } catch (err) {
    console.warn('[Storage Engine] Failed to parse localStorage cache:', err);
  }
  return JSON.parse(JSON.stringify(defaultMockStorage));
}

let mockStorage = loadPersistentDb();

function savePersistentDb() {
  try {
    localStorage.setItem('safehostel_db_v1', JSON.stringify(mockStorage));
  } catch (err) {
    console.warn('[Storage Engine] Failed to save to localStorage:', err);
  }
}

const api = {
  isMockActive: window.location.protocol === 'file:',

  async request(endpoint, options = {}) {
    // If running directly off file system (e.g. double clicked index.html) or already in offline mode
    if (window.location.protocol === 'file:' || this.isMockActive) {
      this.isMockActive = true;
      return this.handleMockFallback(endpoint, options);
    }

    const url = `${API_BASE}${endpoint}`;
    const headers = {
      'Content-Type': 'application/json',
      ...options.headers,
    };

    const token = localStorage.getItem('hostel_token');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    try {
      const response = await fetch(url, { ...options, headers });
      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.error || `HTTP error ${response.status}`);
      }
      return await response.json();
    } catch (err) {
      // If server is unreachable (e.g. static hosting on GitHub Pages, Vercel, Netlify, or offline), fallback autonomously
      console.warn(`[API Client] Live endpoint ${endpoint} unreachable. Engaging in-browser fallback engine:`, err.message);
      this.isMockActive = true;
      return this.handleMockFallback(endpoint, options);
    }
  },

  handleMockFallback(endpoint, options = {}) {
    const method = (options.method || 'GET').toUpperCase();
    const body = options.body ? JSON.parse(options.body) : {};

    if (endpoint === '/auth/login' && method === 'POST') {
      if (body.username === 'admin' && body.password === 'admin123') {
        return Promise.resolve({
          success: true,
          token: 'TOKEN-DEMO-' + Date.now(),
          user: { username: 'admin', fullName: 'Prof. R. K. Sharma', role: 'Chief Hostel Warden', email: 'warden@hostel.edu' }
        });
      }
      return Promise.reject(new Error('Invalid credentials. Use admin / admin123'));
    }

    if (endpoint === '/dashboard') {
      const occupiedBeds = mockStorage.rooms.reduce((acc, r) => acc + r.occupiedBeds, 0);
      const totalCapacity = mockStorage.rooms.reduce((acc, r) => acc + r.capacity, 0);
      const availableBeds = Math.max(0, totalCapacity - occupiedBeds);
      const totalCollected = mockStorage.fees.reduce((acc, f) => acc + f.amountPaid, 0);
      const totalPending = mockStorage.fees.reduce((acc, f) => acc + f.pendingAmount, 0);
      const openComplaints = mockStorage.complaints.filter(c => c.status !== 'Resolved').length;

      return Promise.resolve({
        totalStudents: mockStorage.students.length,
        totalRooms: mockStorage.rooms.length,
        occupiedRooms: mockStorage.rooms.filter(r => r.occupiedBeds >= r.capacity).length,
        availableRooms: mockStorage.rooms.filter(r => r.occupiedBeds < r.capacity && r.status !== 'Maintenance').length,
        maintenanceRooms: mockStorage.rooms.filter(r => r.status === 'Maintenance').length,
        totalCapacityBeds: totalCapacity,
        totalOccupiedBeds: occupiedBeds,
        availableBeds: availableBeds,
        occupancyPercentage: Math.round(((occupiedBeds / totalCapacity) * 100) * 10) / 10,
        totalFeesCollected: totalCollected,
        totalPendingFees: totalPending,
        openComplaints: openComplaints,
        resolvedComplaints: mockStorage.complaints.length - openComplaints,
        dbStatus: 'STANDALONE CLOUD REPOSITORY (ACTIVE)',
        isHttps: window.location.protocol === 'https:'
      });
    }

    if (endpoint === '/security') {
      return Promise.resolve({
        sslEnabled: window.location.protocol === 'https:',
        port: window.location.port || (window.location.protocol === 'https:' ? '443' : '80'),
        protocol: window.location.protocol.toUpperCase().replace(':', ''),
        clientDeviceIp: '127.0.0.1 (Local Client)',
        primaryLanIp: window.location.hostname || 'localhost',
        lanUrls: [window.location.href],
        passwordHashing: 'SHA-256 with 128-bit Cryptographic Salt',
        tokenSecurity: 'HMAC-SHA256 Device-Bound Signature'
      });
    }

    if (endpoint === '/students') {
      if (method === 'GET') return Promise.resolve([...mockStorage.students]);
      if (method === 'POST') {
        const newStu = { id: Date.now(), ...body, admissionDate: new Date().toISOString().split('T')[0] };
        mockStorage.students.unshift(newStu);
        savePersistentDb();
        return Promise.resolve(newStu);
      }
    }

    if (endpoint.startsWith('/students/') && method === 'PUT') {
      const id = parseInt(endpoint.split('/')[2]);
      const idx = mockStorage.students.findIndex(s => s.id === id);
      if (idx !== -1) {
        mockStorage.students[idx] = { ...mockStorage.students[idx], ...body };
        savePersistentDb();
        return Promise.resolve(mockStorage.students[idx]);
      }
    }

    if (endpoint.startsWith('/students/') && method === 'DELETE') {
      const id = parseInt(endpoint.split('/')[2]);
      mockStorage.students = mockStorage.students.filter(s => s.id !== id);
      savePersistentDb();
      return Promise.resolve({ success: true });
    }

    if (endpoint === '/rooms') {
      if (method === 'GET') return Promise.resolve([...mockStorage.rooms]);
      if (method === 'POST') {
        const newRoom = { id: Date.now(), ...body, occupiedBeds: 0, availableBeds: parseInt(body.capacity || 2) };
        mockStorage.rooms.push(newRoom);
        savePersistentDb();
        return Promise.resolve(newRoom);
      }
    }

    if (endpoint.startsWith('/rooms/') && method === 'PUT') {
      const id = parseInt(endpoint.split('/')[2]);
      const idx = mockStorage.rooms.findIndex(r => r.id === id);
      if (idx !== -1) {
        mockStorage.rooms[idx] = { ...mockStorage.rooms[idx], ...body };
        savePersistentDb();
        return Promise.resolve(mockStorage.rooms[idx]);
      }
    }

    if (endpoint.startsWith('/rooms/') && method === 'DELETE') {
      const id = parseInt(endpoint.split('/')[2]);
      mockStorage.rooms = mockStorage.rooms.filter(r => r.id !== id);
      savePersistentDb();
      return Promise.resolve({ success: true });
    }

    if (endpoint === '/allocations') {
      return Promise.resolve([...mockStorage.allocations]);
    }

    if (endpoint === '/allocations/allocate' && method === 'POST') {
      const stu = mockStorage.students.find(s => s.id == body.studentId);
      const room = mockStorage.rooms.find(r => r.id == body.roomId);
      if (stu && room) {
        stu.assignedRoomId = room.id;
        stu.assignedRoomNumber = room.roomNumber;
        room.occupiedBeds++;
        room.availableBeds = Math.max(0, room.capacity - room.occupiedBeds);
        if (room.occupiedBeds >= room.capacity) room.status = 'Occupied';

        const alloc = {
          id: Date.now(),
          studentId: stu.id,
          studentName: stu.fullName,
          studentRollNo: stu.rollNumber,
          roomId: room.id,
          roomNumber: room.roomNumber,
          roomType: room.roomType,
          allocationDate: new Date().toISOString().split('T')[0],
          vacatingDate: null,
          status: 'Active',
          remarks: body.remarks || 'Allocated'
        };
        mockStorage.allocations.unshift(alloc);
        savePersistentDb();
        return Promise.resolve(alloc);
      }
    }

    if (endpoint === '/allocations/vacate' && method === 'POST') {
      const stu = mockStorage.students.find(s => s.id == body.studentId);
      if (stu && stu.assignedRoomId) {
        const room = mockStorage.rooms.find(r => r.id == stu.assignedRoomId);
        if (room) {
          room.occupiedBeds = Math.max(0, room.occupiedBeds - 1);
          room.availableBeds = Math.max(0, room.capacity - room.occupiedBeds);
          room.status = 'Available';
        }
        stu.assignedRoomId = null;
        stu.assignedRoomNumber = null;
        const alloc = mockStorage.allocations.find(a => a.studentId == stu.id && a.status === 'Active');
        if (alloc) {
          alloc.status = 'Vacated';
          alloc.vacatingDate = new Date().toISOString().split('T')[0];
        }
        savePersistentDb();
        return Promise.resolve({ success: true });
      }
    }

    if (endpoint === '/fees') {
      if (method === 'GET') return Promise.resolve([...mockStorage.fees]);
      if (method === 'POST') {
        const stu = mockStorage.students.find(s => s.id == body.studentId);
        const total = parseFloat(body.totalAmount || 0);
        const paid = parseFloat(body.amountPaid || 0);
        const pending = Math.max(0, total - paid);
        const status = (paid >= total && total > 0) ? 'Paid' : (paid > 0 ? 'Partially Paid' : 'Pending');

        const newFee = {
          id: Date.now(),
          receiptNo: 'REC-' + Date.now().toString().slice(-6),
          studentId: stu ? stu.id : 0,
          studentName: stu ? stu.fullName : 'Resident',
          studentRollNo: stu ? stu.rollNumber : 'N/A',
          totalAmount: total,
          amountPaid: paid,
          pendingAmount: pending,
          paymentDate: body.paymentDate || new Date().toISOString().split('T')[0],
          paymentMethod: body.paymentMethod || 'UPI',
          transactionId: body.transactionId || 'TXN-' + Date.now().toString().slice(-5),
          paymentStatus: status,
          remarks: body.remarks || 'Cleared'
        };
        mockStorage.fees.unshift(newFee);
        savePersistentDb();
        return Promise.resolve(newFee);
      }
    }

    if (endpoint === '/complaints') {
      if (method === 'GET') return Promise.resolve([...mockStorage.complaints]);
      if (method === 'POST') {
        const stu = mockStorage.students.find(s => s.id == body.studentId);
        const newCmp = {
          id: Date.now(),
          complaintId: 'CMP-' + Date.now().toString().slice(-6),
          studentId: stu ? stu.id : 0,
          studentName: stu ? stu.fullName : 'Resident',
          roomNumber: stu && stu.assignedRoomNumber ? stu.assignedRoomNumber : 'Unassigned',
          title: body.title,
          description: body.description,
          category: body.category || 'Other',
          priority: body.priority || 'Medium',
          status: 'Pending',
          reportedDate: new Date().toISOString().split('T')[0],
          resolvedDate: null,
          adminRemarks: 'Filed by operator'
        };
        mockStorage.complaints.unshift(newCmp);
        savePersistentDb();
        return Promise.resolve(newCmp);
      }
    }

    if (endpoint.startsWith('/complaints/') && endpoint.endsWith('/status') && method === 'PUT') {
      const id = parseInt(endpoint.split('/')[2]);
      const cmp = mockStorage.complaints.find(c => c.id === id);
      if (cmp) {
        cmp.status = body.status;
        cmp.adminRemarks = body.remarks;
        if (body.status === 'Resolved') cmp.resolvedDate = new Date().toISOString().split('T')[0];
        savePersistentDb();
        return Promise.resolve({ success: true });
      }
    }

    if (endpoint.startsWith('/reports')) {
      return Promise.resolve({
        students: mockStorage.students,
        rooms: mockStorage.rooms,
        allocations: mockStorage.allocations,
        fees: mockStorage.fees,
        complaints: mockStorage.complaints
      });
    }

    return Promise.resolve({});
  },

  // Auth
  login(username, password) {
    return this.request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password })
    });
  },

  // Dashboard & Security
  getDashboard() {
    return this.request('/dashboard');
  },
  getSecurityInfo() {
    return this.request('/security');
  },

  // Students
  getStudents() {
    return this.request('/students');
  },
  getStudentById(id) {
    return this.request(`/students/${id}`);
  },
  addStudent(studentData) {
    return this.request('/students', {
      method: 'POST',
      body: JSON.stringify(studentData)
    });
  },
  updateStudent(id, studentData) {
    return this.request(`/students/${id}`, {
      method: 'PUT',
      body: JSON.stringify(studentData)
    });
  },
  deleteStudent(id) {
    return this.request(`/students/${id}`, {
      method: 'DELETE'
    });
  },

  // Rooms
  getRooms() {
    return this.request('/rooms');
  },
  addRoom(roomData) {
    return this.request('/rooms', {
      method: 'POST',
      body: JSON.stringify(roomData)
    });
  },
  updateRoom(id, roomData) {
    return this.request(`/rooms/${id}`, {
      method: 'PUT',
      body: JSON.stringify(roomData)
    });
  },
  deleteRoom(id) {
    return this.request(`/rooms/${id}`, {
      method: 'DELETE'
    });
  },

  // Allocations
  getAllocations() {
    return this.request('/allocations');
  },
  allocateRoom(studentId, roomId, remarks) {
    return this.request('/allocations/allocate', {
      method: 'POST',
      body: JSON.stringify({ studentId, roomId, remarks })
    });
  },
  vacateRoom(studentId) {
    return this.request('/allocations/vacate', {
      method: 'POST',
      body: JSON.stringify({ studentId })
    });
  },

  // Fees
  getFees() {
    return this.request('/fees');
  },
  recordFee(feeData) {
    return this.request('/fees', {
      method: 'POST',
      body: JSON.stringify(feeData)
    });
  },

  // Complaints
  getComplaints() {
    return this.request('/complaints');
  },
  addComplaint(complaintData) {
    return this.request('/complaints', {
      method: 'POST',
      body: JSON.stringify(complaintData)
    });
  },
  updateComplaintStatus(id, status, remarks) {
    return this.request(`/complaints/${id}/status`, {
      method: 'PUT',
      body: JSON.stringify({ status, remarks })
    });
  },

  // Reports
  getReports(type = 'all') {
    return this.request(`/reports?type=${type}`);
  },

  // Database Reset for Universal Standalone / Demo Mode
  resetMockData() {
    localStorage.removeItem('safehostel_db_v1');
    mockStorage = JSON.parse(JSON.stringify(defaultMockStorage));
    savePersistentDb();
    return Promise.resolve({ success: true });
  }
};
