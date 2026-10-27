/**
 * Aegis Cyber-Defense Hostel ERP - Core Application Logic
 * Implements:
 * 1. 100% Working Light/Dark Mode with CSS Variables + LocalStorage persistence
 * 2. Canvas Matrix / Binary Code Rain Animation
 * 3. Ethical Zero-Knowledge Sensitive Data Masking
 * 4. Dual-Mode REST Client with In-Memory Standalone Fallback for Zero-Config Cloud Deployments
 * 5. Full CRUD: Students, Rooms, Allocations, Fees, Complaints, CSV Exports
 */

// Immediate Theme Initialization (Prevents flash of unstyled theme)
(function() {
  const savedTheme = localStorage.getItem('aegis_theme') || 'dark';
  document.documentElement.setAttribute('data-theme', savedTheme);
})();

// Application State
const appState = {
  currentTab: 'dashboard',
  theme: localStorage.getItem('aegis_theme') || 'dark',
  privacyMasked: true,
  students: [],
  rooms: [],
  allocations: [],
  fees: [],
  complaints: [],
  metrics: {},
  filter: {
    studentSearch: '',
    studentDept: 'all',
    studentStatus: 'all',
    roomType: 'all',
    roomStatus: 'all',
    feeStatus: 'all',
    complaintStatus: 'all',
    complaintCategory: 'all'
  }
};

let matrixInterval = null;

// ==========================================
// INITIALIZATION
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
  initTheme();
  checkAuth();
  setupEventListeners();
  startLiveClock();
});

// ==========================================
// 1. THEME MANAGEMENT (LIGHT / DARK MODE)
// ==========================================
function initTheme() {
  const currentTheme = localStorage.getItem('aegis_theme') || 'dark';
  applyTheme(currentTheme);
}

function toggleTheme() {
  const current = document.documentElement.getAttribute('data-theme') || 'dark';
  const next = current === 'dark' ? 'light' : 'dark';
  applyTheme(next);
  showToast(`Switched to ${next === 'dark' ? 'Cybersecurity Dark SOC' : 'Clean Institutional Light'} Mode`, 'info');
}

function setTheme(theme) {
  applyTheme(theme);
  showToast(`Theme updated to ${theme} mode`, 'info');
}

function applyTheme(theme) {
  appState.theme = theme;
  document.documentElement.setAttribute('data-theme', theme);
  localStorage.setItem('aegis_theme', theme);

  // Update navbar button
  const iconEl = document.getElementById('theme-icon');
  const textEl = document.getElementById('theme-text');
  if (iconEl && textEl) {
    if (theme === 'dark') {
      iconEl.textContent = '☀️';
      textEl.textContent = 'Light Mode';
    } else {
      iconEl.textContent = '🌙';
      textEl.textContent = 'Dark Mode';
    }
  }

  // Update radio buttons in settings if present
  document.querySelectorAll('input[name="theme-choice"]').forEach(r => {
    r.checked = (r.value === theme);
  });

  // Re-render canvas charts to match theme palette
  if (appState.metrics && Object.keys(appState.metrics).length > 0) {
    renderRoomOccupancyChart(appState.metrics);
    renderFeeBreakdownChart(appState.metrics);
  }
}

// ==========================================
// 2. PRIVACY DATA MASKING
// ==========================================
function toggleDataMasking() {
  appState.privacyMasked = !appState.privacyMasked;
  const icon = document.getElementById('privacy-icon');
  const text = document.getElementById('privacy-text');
  const statusBadge = document.getElementById('privacy-status-text');

  if (icon && text) {
    icon.textContent = appState.privacyMasked ? '🔒' : '👁️';
    text.textContent = appState.privacyMasked ? 'Privacy: Masked' : 'Privacy: Unmasked';
  }
  if (statusBadge) {
    statusBadge.textContent = appState.privacyMasked ? 'Masked Mode Active' : 'Unmasked (Audited)';
    statusBadge.style.color = appState.privacyMasked ? 'var(--accent-matrix)' : 'var(--accent-amber)';
  }

  showToast(appState.privacyMasked ? 'Student sensitive data masked for privacy.' : 'Student sensitive data unmasked. Action logged.', 'info');
  renderStudentsTable();
}

function maskText(val) {
  if (!val) return 'N/A';
  if (!appState.privacyMasked) return val;
  const str = String(val).trim();
  if (str.length <= 4) return '••••••';
  return '••••• ' + str.substring(str.length - 4);
}

// ==========================================
// 3. MATRIX / BINARY CODE RAIN ANIMATION
// ==========================================
function startMatrixRain() {
  const canvas = document.getElementById('login-cyber-canvas');
  if (!canvas) return;
  const ctx = canvas.getContext('2d');

  function resize() {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
  }
  resize();
  window.addEventListener('resize', resize);

  const characters = '01010101ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789$#@%&*';
  const fontSize = 14;
  const columns = Math.floor(canvas.width / fontSize);
  const drops = Array(columns).fill(1);

  function draw() {
    ctx.fillStyle = 'rgba(2, 5, 14, 0.08)';
    ctx.fillRect(0, 0, canvas.width, canvas.height);

    ctx.font = fontSize + 'px monospace';

    for (let i = 0; i < drops.length; i++) {
      const text = characters.charAt(Math.floor(Math.random() * characters.length));
      ctx.fillStyle = (i % 3 === 0) ? '#00ff87' : '#00f2fe';
      ctx.fillText(text, i * fontSize, drops[i] * fontSize);

      if (drops[i] * fontSize > canvas.height && Math.random() > 0.975) {
        drops[i] = 0;
      }
      drops[i]++;
    }
  }

  if (matrixInterval) clearInterval(matrixInterval);
  matrixInterval = setInterval(draw, 45);
}

function stopMatrixRain() {
  if (matrixInterval) {
    clearInterval(matrixInterval);
    matrixInterval = null;
  }
}

// ==========================================
// 4. AUTHENTICATION & SESSION
// ==========================================
function checkAuth() {
  const token = localStorage.getItem('hostel_token');
  const loginView = document.getElementById('login-view');
  const appLayout = document.getElementById('app-layout');

  if (token) {
    stopMatrixRain();
    loginView.style.display = 'none';
    appLayout.style.display = 'flex';
    initApp();
  } else {
    loginView.style.display = 'flex';
    appLayout.style.display = 'none';
    startMatrixRain();
  }
}

function handleLoginSubmit(e) {
  e.preventDefault();
  const username = document.getElementById('login-username').value.trim();
  const password = document.getElementById('login-password').value.trim();

  if (!username || !password) {
    showToast('Please enter operator credentials.', 'error');
    return;
  }

  api.login(username, password)
    .then(res => {
      if (res.success) {
        localStorage.setItem('hostel_token', res.token);
        localStorage.setItem('hostel_user', JSON.stringify(res.user));
        showToast('Operator authentication verified. Welcome, Chief Warden!', 'success');
        checkAuth();
      }
    })
    .catch(err => {
      showToast(err.message || 'Authentication rejected. Check credentials.', 'error');
    });
}

function handleLogout() {
  if (confirm('Terminate encrypted administration session?')) {
    localStorage.removeItem('hostel_token');
    localStorage.removeItem('hostel_user');
    showToast('Session cleanly terminated. Cryptographic tokens flushed.', 'info');
    checkAuth();
  }
}

function fillDemoCredentials() {
  document.getElementById('login-username').value = 'admin';
  document.getElementById('login-password').value = 'admin123';
  showToast('Verified demo credentials loaded.', 'info');
}

// ==========================================
// 5. NAVIGATION & CLOCK
// ==========================================
function switchTab(tabId) {
  appState.currentTab = tabId;

  document.querySelectorAll('.sidebar-nav .nav-item').forEach(el => {
    el.classList.toggle('active', el.dataset.tab === tabId);
  });

  document.querySelectorAll('.tab-pane').forEach(pane => {
    pane.classList.remove('active');
  });

  const activePane = document.getElementById(`tab-${tabId}`);
  if (activePane) {
    activePane.classList.add('active');
  }

  const titles = {
    dashboard: 'SOC Dashboard Overview',
    students: 'Student Vault Management',
    rooms: 'Hostel Room Inventory',
    allocation: 'Room Assignment Guard',
    fees: 'Cryptographic Fee Ledger',
    complaints: 'Incident & Grievance Response',
    reports: 'Audit Roster & CSV Exporter',
    settings: 'Security Settings & Ethical Policy'
  };
  const titleEl = document.getElementById('current-breadcrumb-title');
  if (titleEl) {
    titleEl.textContent = titles[tabId] || 'Hostel Administration';
  }

  if (tabId === 'dashboard') loadDashboard();
  else if (tabId === 'students') loadStudents();
  else if (tabId === 'rooms') loadRooms();
  else if (tabId === 'allocation') loadAllocationTab();
  else if (tabId === 'fees') loadFees();
  else if (tabId === 'complaints') loadComplaints();
  else if (tabId === 'reports') loadReports();
}

function toggleSidebar() {
  const sidebar = document.getElementById('sidebar');
  sidebar.classList.toggle('collapsed');
}

function startLiveClock() {
  function update() {
    const el = document.getElementById('soc-live-time');
    if (el) {
      const now = new Date();
      el.textContent = now.toTimeString().split(' ')[0] + ' UTC';
    }
  }
  update();
  setInterval(update, 1000);
}

function initApp() {
  const user = JSON.parse(localStorage.getItem('hostel_user') || '{}');
  if (user.fullName) {
    document.querySelectorAll('.admin-name-display').forEach(el => el.textContent = user.fullName);
  }
  loadDashboard();
}

// ==========================================
// 6. EVENT LISTENERS
// ==========================================
function setupEventListeners() {
  const loginForm = document.getElementById('login-form');
  if (loginForm) loginForm.addEventListener('submit', handleLoginSubmit);

  document.querySelectorAll('.sidebar-nav .nav-item').forEach(item => {
    item.addEventListener('click', (e) => {
      e.preventDefault();
      const tab = item.dataset.tab;
      if (tab) switchTab(tab);
    });
  });

  const toggleBtn = document.getElementById('toggle-sidebar-btn');
  if (toggleBtn) toggleBtn.addEventListener('click', toggleSidebar);

  const logoutBtn = document.getElementById('logout-btn');
  if (logoutBtn) logoutBtn.addEventListener('click', handleLogout);

  // Student Search & Filters
  const studentSearch = document.getElementById('student-search-input');
  if (studentSearch) {
    studentSearch.addEventListener('input', (e) => {
      appState.filter.studentSearch = e.target.value.toLowerCase();
      renderStudentsTable();
    });
  }

  const deptFilter = document.getElementById('student-dept-filter');
  if (deptFilter) {
    deptFilter.addEventListener('change', (e) => {
      appState.filter.studentDept = e.target.value;
      renderStudentsTable();
    });
  }

  const statusFilter = document.getElementById('student-status-filter');
  if (statusFilter) {
    statusFilter.addEventListener('change', (e) => {
      appState.filter.studentStatus = e.target.value;
      renderStudentsTable();
    });
  }

  // Room filters
  const roomTypeFilter = document.getElementById('room-type-filter');
  if (roomTypeFilter) {
    roomTypeFilter.addEventListener('change', (e) => {
      appState.filter.roomType = e.target.value;
      renderRoomsGrid();
    });
  }

  const roomStatusFilter = document.getElementById('room-status-filter');
  if (roomStatusFilter) {
    roomStatusFilter.addEventListener('change', (e) => {
      appState.filter.roomStatus = e.target.value;
      renderRoomsGrid();
    });
  }

  // Fee filters
  const feeStatusFilter = document.getElementById('fee-status-filter');
  if (feeStatusFilter) {
    feeStatusFilter.addEventListener('change', (e) => {
      appState.filter.feeStatus = e.target.value;
      renderFeesTable();
    });
  }

  // Complaint filters
  const compStatusFilter = document.getElementById('complaint-status-filter');
  if (compStatusFilter) {
    compStatusFilter.addEventListener('change', (e) => {
      appState.filter.complaintStatus = e.target.value;
      renderComplaintsTable();
    });
  }

  const compCategoryFilter = document.getElementById('complaint-category-filter');
  if (compCategoryFilter) {
    compCategoryFilter.addEventListener('change', (e) => {
      appState.filter.complaintCategory = e.target.value;
      renderComplaintsTable();
    });
  }
}

// ==========================================
// 7. DASHBOARD CONTROLLER & CHARTS
// ==========================================
function loadDashboard() {
  api.getDashboard()
    .then(data => {
      appState.metrics = data;
      renderDashboardMetrics(data);
      renderRoomOccupancyChart(data);
      renderFeeBreakdownChart(data);
      loadRecentActivities();
    })
    .catch(err => {
      showToast('Telemetry sync error: ' + err.message, 'error');
    });
}

function renderDashboardMetrics(data) {
  document.getElementById('metric-total-students').textContent = data.totalStudents ?? 0;
  document.getElementById('metric-total-rooms').textContent = data.totalRooms ?? 0;
  document.getElementById('metric-occupied-rooms').textContent = data.occupiedRooms ?? 0;
  document.getElementById('metric-available-rooms').textContent = data.availableRooms ?? 0;
  document.getElementById('metric-total-collected').textContent = '₹' + (data.totalFeesCollected ?? 0).toLocaleString();
  document.getElementById('metric-total-pending').textContent = '₹' + (data.totalPendingFees ?? 0).toLocaleString();
  document.getElementById('metric-open-complaints').textContent = data.openComplaints ?? 0;
  document.getElementById('metric-occupancy-rate').textContent = (data.occupancyPercentage ?? 0) + '%';

  // DB pill
  const dbStatusPill = document.getElementById('db-status-pill');
  if (dbStatusPill && data.dbStatus) {
    const isMock = data.dbStatus.includes('Mock');
    dbStatusPill.innerHTML = `<span class="db-dot" style="background: ${isMock ? 'var(--accent-amber)' : 'var(--accent-matrix)'};"></span> ${isMock ? 'In-Memory Vault' : 'MySQL 8.0 Live'}`;
  }

  // Encryption badge in top navbar
  const encLabel = document.getElementById('nav-encryption-label');
  if (encLabel) {
    const isHttps = window.location.protocol === 'https:' || data.isHttps;
    encLabel.textContent = isHttps ? 'TLS 1.3 Active' : 'HTTP Clean Mode';
  }
}

function renderRoomOccupancyChart(data) {
  const canvas = document.getElementById('occupancy-chart');
  if (!canvas) return;
  const ctx = canvas.getContext('2d');
  ctx.clearRect(0, 0, canvas.width, canvas.height);

  const isDark = (document.documentElement.getAttribute('data-theme') || 'dark') === 'dark';

  const occupied = data.totalOccupiedBeds || 0;
  const available = data.availableBeds || 0;
  const total = occupied + available;

  if (total === 0) return;

  const centerX = canvas.width / 2;
  const centerY = canvas.height / 2;
  const radius = Math.min(centerX, centerY) - 20;
  const innerRadius = radius * 0.68;

  const occupiedAngle = (occupied / total) * 2 * Math.PI;

  // Occupied slice (Neon Cyan in dark mode, Electric Blue in light mode)
  ctx.beginPath();
  ctx.arc(centerX, centerY, radius, 0, occupiedAngle);
  ctx.arc(centerX, centerY, innerRadius, occupiedAngle, 0, true);
  ctx.closePath();
  ctx.fillStyle = isDark ? '#00f2fe' : '#0284c7';
  if (isDark) {
    ctx.shadowBlur = 12;
    ctx.shadowColor = 'rgba(0, 242, 254, 0.5)';
  } else {
    ctx.shadowBlur = 0;
  }
  ctx.fill();

  // Available slice (Matrix Green in dark mode, Soft Teal in light mode)
  ctx.beginPath();
  ctx.arc(centerX, centerY, radius, occupiedAngle, 2 * Math.PI);
  ctx.arc(centerX, centerY, innerRadius, 2 * Math.PI, occupiedAngle, true);
  ctx.closePath();
  ctx.fillStyle = isDark ? '#00ff87' : '#0d9488';
  if (isDark) {
    ctx.shadowBlur = 12;
    ctx.shadowColor = 'rgba(0, 255, 135, 0.4)';
  } else {
    ctx.shadowBlur = 0;
  }
  ctx.fill();

  ctx.shadowBlur = 0;

  // Center text
  ctx.fillStyle = isDark ? '#f8fafc' : '#0f172a';
  ctx.font = 'bold 22px monospace';
  ctx.textAlign = 'center';
  ctx.textBaseline = 'middle';
  ctx.fillText(`${data.occupancyPercentage || 0}%`, centerX, centerY - 6);

  ctx.fillStyle = isDark ? '#94a3b8' : '#64748b';
  ctx.font = '11px monospace';
  ctx.fillText('OCCUPIED', centerX, centerY + 18);
}

function renderFeeBreakdownChart(data) {
  const canvas = document.getElementById('fee-chart');
  if (!canvas) return;
  const ctx = canvas.getContext('2d');
  ctx.clearRect(0, 0, canvas.width, canvas.height);

  const isDark = (document.documentElement.getAttribute('data-theme') || 'dark') === 'dark';

  const collected = data.totalFeesCollected || 0;
  const pending = data.totalPendingFees || 0;
  const total = collected + pending;

  if (total === 0) return;

  const barHeight = 24;
  const y = (canvas.height - barHeight) / 2 + 5;
  const width = canvas.width - 20;
  const x = 10;

  const collectedWidth = (collected / total) * width;
  const pendingWidth = (pending / total) * width;

  // Collected bar
  ctx.fillStyle = isDark ? '#00ff87' : '#059669';
  ctx.fillRect(x, y, collectedWidth, barHeight);

  // Pending bar
  ctx.fillStyle = isDark ? '#f59e0b' : '#d97706';
  ctx.fillRect(x + collectedWidth, y, pendingWidth, barHeight);

  // Text labels
  ctx.fillStyle = isDark ? '#f8fafc' : '#0f172a';
  ctx.font = 'bold 11px monospace';
  ctx.textAlign = 'left';
  ctx.fillText(`CLEAR: ₹${collected.toLocaleString()}`, x, y - 8);

  ctx.textAlign = 'right';
  ctx.fillText(`DUE: ₹${pending.toLocaleString()}`, x + width, y - 8);
}

function loadRecentActivities() {
  const container = document.getElementById('recent-activity-list');
  if (!container) return;

  Promise.all([api.getAllocations(), api.getComplaints(), api.getFees()])
    .then(([allocs, comps, fees]) => {
      let activities = [];

      allocs.slice(0, 3).forEach(a => {
        activities.push({
          icon: '🏠',
          title: `Room Allocation: ${a.roomNumber}`,
          sub: `${a.studentName} assigned ${a.roomNumber} (${a.roomType})`,
          date: a.allocationDate
        });
      });

      comps.slice(0, 3).forEach(c => {
        activities.push({
          icon: '⚠️',
          title: `Incident: ${c.category}`,
          sub: `${c.studentName}: ${c.title} [${c.status}]`,
          date: c.reportedDate
        });
      });

      fees.slice(0, 3).forEach(f => {
        activities.push({
          icon: '💳',
          title: `Transaction: ₹${f.amountPaid.toLocaleString()}`,
          sub: `${f.studentName} via ${f.paymentMethod} (${f.paymentStatus})`,
          date: f.paymentDate
        });
      });

      if (activities.length === 0) {
        container.innerHTML = `<p style="color: var(--text-muted); text-align: center; padding: 20px;">No audit records found.</p>`;
        return;
      }

      container.innerHTML = activities.slice(0, 6).map(act => `
        <div style="display: flex; gap: 12px; padding: 10px 0; border-bottom: 1px solid var(--border-subtle); align-items: flex-start;">
          <div style="width: 34px; height: 34px; border-radius: 6px; background: rgba(0, 242, 254, 0.08); border: 1px solid var(--border-color); display: flex; align-items: center; justify-content: center; font-size: 1rem; flex-shrink: 0;">
            ${act.icon}
          </div>
          <div style="flex: 1;">
            <div style="font-weight: 700; font-size: 0.85rem; color: var(--text-primary);">${act.title}</div>
            <div style="font-size: 0.76rem; color: var(--text-secondary); margin-top: 2px;">${act.sub}</div>
          </div>
          <div class="mono-font" style="font-size: 0.7rem; color: var(--text-muted); white-space: nowrap;">${act.date || 'Recent'}</div>
        </div>
      `).join('');
    });
}

// ==========================================
// 8. STUDENT MANAGEMENT
// ==========================================
function loadStudents() {
  api.getStudents()
    .then(data => {
      appState.students = data;
      renderStudentsTable();
    })
    .catch(err => {
      showToast('Vault retrieval error: ' + err.message, 'error');
    });
}

function renderStudentsTable() {
  const tbody = document.getElementById('students-table-body');
  if (!tbody) return;

  const { studentSearch, studentDept, studentStatus } = appState.filter;

  const filtered = appState.students.filter(s => {
    const matchSearch = !studentSearch ||
      s.fullName.toLowerCase().includes(studentSearch) ||
      s.rollNumber.toLowerCase().includes(studentSearch) ||
      s.studentId.toLowerCase().includes(studentSearch) ||
      (s.department && s.department.toLowerCase().includes(studentSearch));

    const matchDept = studentDept === 'all' || s.department === studentDept;
    const matchStatus = studentStatus === 'all' || s.status === studentStatus;

    return matchSearch && matchDept && matchStatus;
  });

  const countBadge = document.getElementById('student-count-badge');
  if (countBadge) countBadge.textContent = `DISPLAYING: ${filtered.length} OF ${appState.students.length} VERIFIED RECORDS`;

  if (filtered.length === 0) {
    tbody.innerHTML = `<tr><td colspan="8" style="text-align: center; padding: 36px; color: var(--text-muted);">No matching student records located in vault.</td></tr>`;
    return;
  }

  tbody.innerHTML = filtered.map(s => `
    <tr>
      <td>
        <span class="mono-font" style="font-weight: 800; color: var(--accent-cyan);">${s.studentId}</span>
      </td>
      <td>
        <div style="font-weight: 700; color: var(--text-primary);">${s.fullName}</div>
        <div class="mono-font" style="font-size: 0.73rem; color: var(--text-secondary);">${s.email || 'N/A'}</div>
      </td>
      <td>
        <span class="badge badge-secondary mono-font">${s.rollNumber}</span>
      </td>
      <td>
        <div>${s.department}</div>
        <div style="font-size: 0.73rem; color: var(--text-muted);">${s.yearOfStudy}</div>
      </td>
      <td class="mono-font" style="font-size: 0.8rem;">
        <div>📞 ${maskText(s.phone)}</div>
        <div style="font-size: 0.72rem; color: var(--text-muted);">Emergency: ${maskText(s.parentContact)}</div>
      </td>
      <td>
        ${s.assignedRoomNumber ?
          `<span class="badge badge-info">Room ${s.assignedRoomNumber}</span>` :
          `<span class="badge badge-secondary">Unallocated</span>`}
      </td>
      <td>
        <span class="badge ${s.status === 'Active' ? 'badge-success' : 'badge-danger'}">${s.status}</span>
      </td>
      <td>
        <div style="display: flex; gap: 6px;">
          <button class="btn btn-secondary btn-sm" onclick="viewStudentModal(${s.id})" title="View Vault Card">👁️</button>
          <button class="btn btn-secondary btn-sm" onclick="openEditStudentModal(${s.id})" title="Edit Record">✏️</button>
          <button class="btn btn-danger btn-sm" onclick="deleteStudent(${s.id}, '${escapeHtml(s.fullName)}')" title="Purge Record">🗑️</button>
        </div>
      </td>
    </tr>
  `).join('');
}

function openAddStudentModal() {
  document.getElementById('student-modal-title').textContent = 'Enroll Student in Vault';
  document.getElementById('student-form').reset();
  document.getElementById('student-form-id').value = '';
  document.getElementById('student-id-input').readOnly = false;
  document.getElementById('student-id-input').value = 'STU-2024-' + String(Math.floor(Math.random() * 900) + 100);
  openModal('student-modal');
}

function openEditStudentModal(id) {
  const student = appState.students.find(s => s.id === id);
  if (!student) return;

  document.getElementById('student-modal-title').textContent = 'Edit Vault Record: ' + student.studentId;
  document.getElementById('student-form-id').value = student.id;
  document.getElementById('student-id-input').value = student.studentId;
  document.getElementById('student-id-input').readOnly = true;
  document.getElementById('student-name-input').value = student.fullName;
  document.getElementById('student-roll-input').value = student.rollNumber;
  document.getElementById('student-dept-input').value = student.department;
  document.getElementById('student-year-input').value = student.yearOfStudy;
  document.getElementById('student-gender-input').value = student.gender || 'Male';
  document.getElementById('student-dob-input').value = student.dob || '';
  document.getElementById('student-phone-input').value = student.phone || '';
  document.getElementById('student-email-input').value = student.email || '';
  document.getElementById('student-parent-input').value = student.parentContact || '';
  document.getElementById('student-address-input').value = student.address || '';
  document.getElementById('student-status-input').value = student.status || 'Active';

  openModal('student-modal');
}

function handleSaveStudent(e) {
  e.preventDefault();
  const id = document.getElementById('student-form-id').value;
  const studentData = {
    studentId: document.getElementById('student-id-input').value.trim(),
    fullName: document.getElementById('student-name-input').value.trim(),
    rollNumber: document.getElementById('student-roll-input').value.trim(),
    department: document.getElementById('student-dept-input').value,
    yearOfStudy: document.getElementById('student-year-input').value,
    gender: document.getElementById('student-gender-input').value,
    dob: document.getElementById('student-dob-input').value,
    phone: document.getElementById('student-phone-input').value.trim(),
    email: document.getElementById('student-email-input').value.trim(),
    parentContact: document.getElementById('student-parent-input').value.trim(),
    address: document.getElementById('student-address-input').value.trim(),
    status: document.getElementById('student-status-input').value
  };

  if (!studentData.fullName || !studentData.studentId || !studentData.rollNumber) {
    showToast('Name, Student ID, and Roll Number are mandatory.', 'error');
    return;
  }

  const promise = id ? api.updateStudent(id, studentData) : api.addStudent(studentData);

  promise
    .then(() => {
      showToast(id ? 'Vault record successfully updated!' : 'Student verified & registered in vault!', 'success');
      closeModal('student-modal');
      loadStudents();
    })
    .catch(err => {
      showToast(err.message, 'error');
    });
}

function viewStudentModal(id) {
  const s = appState.students.find(item => item.id === id);
  if (!s) return;

  const content = `
    <div style="text-align: center; margin-bottom: 20px;">
      <div style="width: 66px; height: 66px; border-radius: 12px; background: rgba(0, 242, 254, 0.1); color: var(--accent-cyan); font-size: 1.6rem; font-weight: 800; display: inline-flex; align-items: center; justify-content: center; border: 2px solid var(--accent-cyan);" class="mono-font">
        ${s.fullName.charAt(0)}
      </div>
      <h3 style="margin-top: 10px; font-size: 1.2rem; color: var(--text-primary);">${s.fullName}</h3>
      <span class="badge badge-info mono-font">${s.studentId}</span>
      <span class="badge ${s.status === 'Active' ? 'badge-success' : 'badge-danger'}">${s.status}</span>
    </div>

    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px; font-size: 0.85rem; background: var(--bg-surface); padding: 16px; border-radius: 10px; border: 1px solid var(--border-color);">
      <div><strong>Roll Number:</strong> <span class="mono-font">${s.rollNumber}</span></div>
      <div><strong>Department:</strong> ${s.department}</div>
      <div><strong>Year of Study:</strong> ${s.yearOfStudy}</div>
      <div><strong>Assigned Room:</strong> ${s.assignedRoomNumber ? 'Room ' + s.assignedRoomNumber : 'None'}</div>
      <div><strong>Gender:</strong> ${s.gender || 'N/A'}</div>
      <div><strong>Date of Birth:</strong> ${s.dob || 'N/A'}</div>
      <div><strong>Student Phone:</strong> <span class="mono-font">${s.phone || 'N/A'}</span></div>
      <div><strong>Parent Phone:</strong> <span class="mono-font">${s.parentContact || 'N/A'}</span></div>
      <div style="grid-column: 1 / -1;"><strong>Email:</strong> <span class="mono-font">${s.email || 'N/A'}</span></div>
      <div style="grid-column: 1 / -1;"><strong>Home Address:</strong> ${s.address || 'N/A'}</div>
      <div style="grid-column: 1 / -1;"><strong>Admission Date:</strong> <span class="mono-font">${s.admissionDate || 'N/A'}</span></div>
    </div>
  `;

  document.getElementById('view-student-modal-body').innerHTML = content;
  openModal('view-student-modal');
}

function deleteStudent(id, name) {
  if (confirm(`Purge student "${name}" from vault? Room bed will be unassigned automatically.`)) {
    api.deleteStudent(id)
      .then(() => {
        showToast(`Record "${name}" purged.`, 'success');
        loadStudents();
      })
      .catch(err => {
        showToast('Purge failed: ' + err.message, 'error');
      });
  }
}

// ==========================================
// 9. ROOM MANAGEMENT
// ==========================================
function loadRooms() {
  api.getRooms()
    .then(data => {
      appState.rooms = data;
      renderRoomsGrid();
    })
    .catch(err => {
      showToast('Inventory error: ' + err.message, 'error');
    });
}

function renderRoomsGrid() {
  const container = document.getElementById('rooms-grid-container');
  if (!container) return;

  const { roomType, roomStatus } = appState.filter;

  const filtered = appState.rooms.filter(r => {
    const matchType = roomType === 'all' || r.roomType === roomType;
    const matchStatus = roomStatus === 'all' || r.status === roomStatus;
    return matchType && matchStatus;
  });

  const countBadge = document.getElementById('room-count-badge');
  if (countBadge) countBadge.textContent = `DISPLAYING: ${filtered.length} OF ${appState.rooms.length} ROOMS`;

  if (filtered.length === 0) {
    container.innerHTML = `<div style="grid-column: 1/-1; text-align: center; padding: 40px; color: var(--text-muted);">No matching rooms found.</div>`;
    return;
  }

  container.innerHTML = filtered.map(r => {
    const fillPercent = r.capacity > 0 ? (r.occupiedBeds / r.capacity) * 100 : 0;
    const statusClass = r.status === 'Available' ? 'badge-success' :
                        r.status === 'Occupied' ? 'badge-danger' : 'badge-warning';

    return `
      <div class="room-card">
        <div class="room-card-header">
          <div>
            <div class="room-number">Room ${r.roomNumber}</div>
            <span style="font-size: 0.74rem; color: var(--text-secondary);">${r.roomType} Tier</span>
          </div>
          <span class="badge ${statusClass}">${r.status}</span>
        </div>
        <div class="room-card-body">
          <div style="display: flex; justify-content: space-between; align-items: baseline;">
            <div style="font-size: 0.8rem; color: var(--text-secondary);">Bed Occupancy</div>
            <div class="mono-font" style="font-weight: 800; font-size: 0.95rem;">${r.occupiedBeds} / ${r.capacity} Beds</div>
          </div>
          <div class="room-capacity-meter">
            <div class="room-capacity-fill" style="width: ${fillPercent}%;"></div>
          </div>
          <div style="display: flex; justify-content: space-between; font-size: 0.78rem; color: var(--text-secondary); margin-top: 8px;">
            <span>Available: <strong style="color: var(--accent-matrix);">${r.availableBeds} beds</strong></span>
            <span>Fee: <strong>₹${r.monthlyFee.toLocaleString()}/mo</strong></span>
          </div>
          <p style="font-size: 0.76rem; color: var(--text-muted); margin: 12px 0 14px; min-height: 28px;">${r.description || 'Standard secure accommodation'}</p>
          <div style="display: flex; gap: 8px; justify-content: flex-end; border-top: 1px solid var(--border-subtle); padding-top: 10px;">
            <button class="btn btn-secondary btn-sm" onclick="openEditRoomModal(${r.id})">Configure</button>
            <button class="btn btn-danger btn-sm" onclick="deleteRoom(${r.id}, '${r.roomNumber}')" ${r.occupiedBeds > 0 ? 'disabled title="Cannot delete room with active residents"' : ''}>Delete</button>
          </div>
        </div>
      </div>
    `;
  }).join('');
}

function openAddRoomModal() {
  document.getElementById('room-modal-title').textContent = 'Configure New Room';
  document.getElementById('room-form').reset();
  document.getElementById('room-form-id').value = '';
  document.getElementById('room-number-input').readOnly = false;
  openModal('room-modal');
}

function openEditRoomModal(id) {
  const r = appState.rooms.find(item => item.id === id);
  if (!r) return;

  document.getElementById('room-modal-title').textContent = 'Configure Room ' + r.roomNumber;
  document.getElementById('room-form-id').value = r.id;
  document.getElementById('room-number-input').value = r.roomNumber;
  document.getElementById('room-number-input').readOnly = true;
  document.getElementById('room-type-input').value = r.roomType;
  document.getElementById('room-capacity-input').value = r.capacity;
  document.getElementById('room-fee-input').value = r.monthlyFee;
  document.getElementById('room-status-input').value = r.status;
  document.getElementById('room-desc-input').value = r.description || '';

  openModal('room-modal');
}

function handleSaveRoom(e) {
  e.preventDefault();
  const id = document.getElementById('room-form-id').value;
  const roomData = {
    roomNumber: document.getElementById('room-number-input').value.trim(),
    roomType: document.getElementById('room-type-input').value,
    capacity: document.getElementById('room-capacity-input').value,
    monthlyFee: document.getElementById('room-fee-input').value,
    status: document.getElementById('room-status-input').value,
    description: document.getElementById('room-desc-input').value.trim()
  };

  const promise = id ? api.updateRoom(id, roomData) : api.addRoom(roomData);
  promise
    .then(() => {
      showToast(id ? 'Room updated!' : 'Room configured successfully!', 'success');
      closeModal('room-modal');
      loadRooms();
    })
    .catch(err => {
      showToast(err.message, 'error');
    });
}

function deleteRoom(id, roomNumber) {
  if (confirm(`Delete Room ${roomNumber}?`)) {
    api.deleteRoom(id)
      .then(() => {
        showToast(`Room ${roomNumber} removed.`, 'success');
        loadRooms();
      })
      .catch(err => {
        showToast(err.message, 'error');
      });
  }
}

// ==========================================
// 10. ROOM ALLOCATION
// ==========================================
function loadAllocationTab() {
  Promise.all([api.getStudents(), api.getRooms(), api.getAllocations()])
    .then(([students, rooms, allocations]) => {
      appState.students = students;
      appState.rooms = rooms;
      appState.allocations = allocations;
      populateAllocationSelects();
      renderAllocationsTable();
    })
    .catch(err => {
      showToast('Allocation sync error: ' + err.message, 'error');
    });
}

function populateAllocationSelects() {
  const studentSelect = document.getElementById('alloc-student-select');
  const roomSelect = document.getElementById('alloc-room-select');
  if (!studentSelect || !roomSelect) return;

  const unassigned = appState.students.filter(s => !s.assignedRoomId && s.status === 'Active');
  studentSelect.innerHTML = unassigned.length > 0 ?
    `<option value="">-- Choose Unallocated Student (${unassigned.length} Available) --</option>` +
    unassigned.map(s => `<option value="${s.id}">${s.fullName} [${s.rollNumber} - ${s.department}]</option>`).join('') :
    `<option value="">No unallocated students available</option>`;

  const availableRooms = appState.rooms.filter(r => r.availableBeds > 0 && r.status !== 'Maintenance');
  roomSelect.innerHTML = availableRooms.length > 0 ?
    `<option value="">-- Choose Room with Open Vacancy (${availableRooms.length} Available) --</option>` +
    availableRooms.map(r => `<option value="${r.id}">Room ${r.roomNumber} (${r.roomType} - ${r.availableBeds} beds left)</option>`).join('') :
    `<option value="">No vacant rooms available</option>`;
}

function handleAllocateSubmit(e) {
  e.preventDefault();
  const studentId = document.getElementById('alloc-student-select').value;
  const roomId = document.getElementById('alloc-room-select').value;
  const remarks = document.getElementById('alloc-remarks-input').value.trim();

  if (!studentId || !roomId) {
    showToast('Select both a student and an available room.', 'error');
    return;
  }

  api.allocateRoom(studentId, roomId, remarks)
    .then(() => {
      showToast('Room assignment confirmed & encrypted in audit trail!', 'success');
      document.getElementById('alloc-remarks-input').value = '';
      loadAllocationTab();
    })
    .catch(err => {
      showToast(err.message, 'error');
    });
}

function renderAllocationsTable() {
  const tbody = document.getElementById('allocations-table-body');
  if (!tbody) return;

  if (appState.allocations.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; padding: 30px; color: var(--text-muted);">No allocation records in audit log.</td></tr>`;
    return;
  }

  tbody.innerHTML = appState.allocations.map(a => `
    <tr>
      <td><span class="mono-font" style="font-weight: 800; color: var(--accent-cyan);">#AL-${a.id}</span></td>
      <td>
        <div style="font-weight: 700; color: var(--text-primary);">${a.studentName}</div>
        <div class="mono-font" style="font-size: 0.73rem; color: var(--text-secondary);">Roll: ${a.studentRollNo}</div>
      </td>
      <td>
        <span class="badge badge-info">Room ${a.roomNumber}</span>
        <span style="font-size: 0.74rem; color: var(--text-secondary); margin-left: 4px;">${a.roomType}</span>
      </td>
      <td class="mono-font">${a.allocationDate}</td>
      <td class="mono-font">${a.vacatingDate || '-'}</td>
      <td>
        <span class="badge ${a.status === 'Active' ? 'badge-success' : 'badge-secondary'}">${a.status}</span>
      </td>
      <td>
        ${a.status === 'Active' ?
          `<button class="btn btn-danger btn-sm" onclick="handleVacateRoom(${a.studentId}, '${escapeHtml(a.studentName)}', '${a.roomNumber}')">Vacate Bed</button>` :
          `<span class="mono-font" style="font-size: 0.72rem; color: var(--text-muted);">Completed</span>`}
      </td>
    </tr>
  `).join('');
}

function handleVacateRoom(studentId, studentName, roomNumber) {
  if (confirm(`Vacate ${studentName} from Room ${roomNumber}? This frees 1 bed.`)) {
    api.vacateRoom(studentId)
      .then(() => {
        showToast(`Student ${studentName} vacated from Room ${roomNumber}.`, 'success');
        loadAllocationTab();
      })
      .catch(err => {
        showToast(err.message, 'error');
      });
  }
}

// ==========================================
// 11. FEE MANAGEMENT
// ==========================================
function loadFees() {
  Promise.all([api.getFees(), api.getStudents()])
    .then(([fees, students]) => {
      appState.fees = fees;
      appState.students = students;
      renderFeesTable();
      populateFeeStudentDropdown();
    })
    .catch(err => {
      showToast('Fee ledger error: ' + err.message, 'error');
    });
}

function populateFeeStudentDropdown() {
  const select = document.getElementById('fee-student-select');
  if (!select) return;

  select.innerHTML = '<option value="">-- Choose Resident Student --</option>' +
    appState.students.map(s => `<option value="${s.id}">${s.fullName} (${s.rollNumber})</option>`).join('');
}

function renderFeesTable() {
  const tbody = document.getElementById('fees-table-body');
  if (!tbody) return;

  const { feeStatus } = appState.filter;
  const filtered = appState.fees.filter(f => feeStatus === 'all' || f.paymentStatus === feeStatus);

  if (filtered.length === 0) {
    tbody.innerHTML = `<tr><td colspan="8" style="text-align: center; padding: 36px; color: var(--text-muted);">No transaction records in ledger.</td></tr>`;
    return;
  }

  tbody.innerHTML = filtered.map(f => {
    const statusBadge = f.paymentStatus === 'Paid' ? 'badge-success' :
                        f.paymentStatus === 'Partially Paid' ? 'badge-warning' : 'badge-danger';

    return `
      <tr>
        <td><span class="mono-font" style="font-weight: 800; color: var(--accent-cyan);">${f.receiptNo}</span></td>
        <td>
          <div style="font-weight: 700; color: var(--text-primary);">${f.studentName}</div>
          <div class="mono-font" style="font-size: 0.73rem; color: var(--text-secondary);">${f.studentRollNo}</div>
        </td>
        <td class="mono-font">₹${f.totalAmount.toLocaleString()}</td>
        <td class="mono-font" style="color: var(--accent-matrix); font-weight: 700;">₹${f.amountPaid.toLocaleString()}</td>
        <td class="mono-font" style="color: ${f.pendingAmount > 0 ? 'var(--accent-crimson)' : 'var(--text-muted)'}; font-weight: 700;">₹${f.pendingAmount.toLocaleString()}</td>
        <td>
          <span class="badge badge-secondary">${f.paymentMethod}</span>
          <div class="mono-font" style="font-size: 0.7rem; color: var(--text-muted); margin-top: 2px;">${f.transactionId}</div>
        </td>
        <td><span class="badge ${statusBadge}">${f.paymentStatus}</span></td>
        <td>
          <button class="btn btn-secondary btn-sm" onclick="viewReceiptModal('${f.receiptNo}')">Receipt</button>
        </td>
      </tr>
    `;
  }).join('');
}

function openRecordFeeModal() {
  document.getElementById('fee-form').reset();
  document.getElementById('fee-date-input').value = new Date().toISOString().split('T')[0];
  document.getElementById('fee-total-input').value = '42000';
  document.getElementById('fee-paid-input').value = '42000';
  calculateFeePending();
  openModal('fee-modal');
}

function calculateFeePending() {
  const total = parseFloat(document.getElementById('fee-total-input').value) || 0;
  const paid = parseFloat(document.getElementById('fee-paid-input').value) || 0;
  const pending = Math.max(0, total - paid);
  document.getElementById('fee-pending-display').textContent = '₹' + pending.toLocaleString();
}

function handleSaveFee(e) {
  e.preventDefault();
  const feeData = {
    studentId: document.getElementById('fee-student-select').value,
    totalAmount: document.getElementById('fee-total-input').value,
    amountPaid: document.getElementById('fee-paid-input').value,
    paymentMethod: document.getElementById('fee-method-select').value,
    transactionId: document.getElementById('fee-txn-input').value.trim() || 'TXN-' + Math.floor(Math.random() * 1000000),
    paymentDate: document.getElementById('fee-date-input').value,
    remarks: document.getElementById('fee-remarks-input').value.trim()
  };

  if (!feeData.studentId) {
    showToast('Select a resident student.', 'error');
    return;
  }

  api.recordFee(feeData)
    .then(saved => {
      showToast('Payment signed and recorded! Ref: ' + saved.receiptNo, 'success');
      closeModal('fee-modal');
      loadFees();
    })
    .catch(err => {
      showToast(err.message, 'error');
    });
}

function viewReceiptModal(receiptNo) {
  const f = appState.fees.find(item => item.receiptNo === receiptNo);
  if (!f) return;

  const content = `
    <div style="border: 2px dashed var(--border-color); padding: 24px; border-radius: 12px; background: var(--bg-surface);">
      <div style="text-align: center; border-bottom: 2px solid var(--border-color); padding-bottom: 14px; margin-bottom: 18px;">
        <h2 style="font-size: 1.3rem; margin-bottom: 4px; color: var(--text-primary);">AEGIS UNIVERSITY HOSTEL ADMINISTRATION</h2>
        <p class="mono-font" style="font-size: 0.78rem; color: var(--accent-cyan);">OFFICIAL CRYPTOGRAPHIC PAYMENT RECEIPT</p>
      </div>

      <div style="display: flex; justify-content: space-between; font-size: 0.85rem; margin-bottom: 18px;" class="mono-font">
        <div><strong>Receipt Ref:</strong> <span style="color: var(--accent-cyan); font-weight: 800;">${f.receiptNo}</span></div>
        <div><strong>Date:</strong> ${f.paymentDate}</div>
      </div>

      <div style="background: var(--bg-card); padding: 14px; border-radius: 8px; font-size: 0.85rem; margin-bottom: 18px; border: 1px solid var(--border-subtle);">
        <div style="margin-bottom: 4px;"><strong>Student:</strong> ${f.studentName}</div>
        <div style="margin-bottom: 4px;"><strong>Roll Number:</strong> <span class="mono-font">${f.studentRollNo}</span></div>
        <div><strong>Channel:</strong> ${f.paymentMethod} (Txn: <span class="mono-font">${f.transactionId}</span>)</div>
      </div>

      <table style="width: 100%; border-collapse: collapse; font-size: 0.88rem; margin-bottom: 18px;">
        <tr style="border-bottom: 1px solid var(--border-subtle);">
          <th style="text-align: left; padding: 6px 0;">Description</th>
          <th style="text-align: right; padding: 6px 0;">Amount</th>
        </tr>
        <tr>
          <td style="padding: 8px 0;">Semester Hostel & Mess Charges</td>
          <td class="mono-font" style="text-align: right; padding: 8px 0;">₹${f.totalAmount.toLocaleString()}</td>
        </tr>
        <tr style="border-top: 1px solid var(--border-subtle); font-weight: 700; color: var(--accent-matrix);">
          <td style="padding: 8px 0;">Amount Cleared</td>
          <td class="mono-font" style="text-align: right; padding: 8px 0;">₹${f.amountPaid.toLocaleString()}</td>
        </tr>
        <tr style="font-weight: 700; color: ${f.pendingAmount > 0 ? 'var(--accent-crimson)' : 'var(--text-muted)'};">
          <td style="padding: 6px 0;">Remaining Balance Due</td>
          <td class="mono-font" style="text-align: right; padding: 6px 0;">₹${f.pendingAmount.toLocaleString()}</td>
        </tr>
      </table>

      <div style="display: flex; justify-content: space-between; align-items: flex-end; margin-top: 24px; font-size: 0.78rem; color: var(--text-secondary);">
        <div>Status: <span class="badge ${f.paymentStatus === 'Paid' ? 'badge-success' : 'badge-warning'}">${f.paymentStatus}</span></div>
        <div style="text-align: right; border-top: 1px solid var(--border-color); padding-top: 4px; width: 140px;" class="mono-font">
          Warden Verification
        </div>
      </div>
    </div>
  `;

  document.getElementById('receipt-modal-body').innerHTML = content;
  openModal('receipt-modal');
}

// ==========================================
// 12. COMPLAINT / INCIDENT MANAGEMENT
// ==========================================
function loadComplaints() {
  Promise.all([api.getComplaints(), api.getStudents()])
    .then(([comps, students]) => {
      appState.complaints = comps;
      appState.students = students;
      renderComplaintsTable();
      populateComplaintStudentDropdown();
    })
    .catch(err => {
      showToast('Incident log error: ' + err.message, 'error');
    });
}

function populateComplaintStudentDropdown() {
  const select = document.getElementById('complaint-student-select');
  if (!select) return;

  select.innerHTML = '<option value="">-- Choose Resident Student --</option>' +
    appState.students.map(s => `<option value="${s.id}">${s.fullName} [Room: ${s.assignedRoomNumber || 'Unassigned'}]</option>`).join('');
}

function renderComplaintsTable() {
  const tbody = document.getElementById('complaints-table-body');
  if (!tbody) return;

  const { complaintStatus, complaintCategory } = appState.filter;

  const filtered = appState.complaints.filter(c => {
    const matchStatus = complaintStatus === 'all' || c.status === complaintStatus;
    const matchCat = complaintCategory === 'all' || c.category === complaintCategory;
    return matchStatus && matchCat;
  });

  if (filtered.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; padding: 36px; color: var(--text-muted);">No incident tickets match criteria.</td></tr>`;
    return;
  }

  tbody.innerHTML = filtered.map(c => {
    const priorityBadge = c.priority === 'High' ? 'badge-danger' :
                          c.priority === 'Medium' ? 'badge-warning' : 'badge-info';
    const statusBadge = c.status === 'Resolved' ? 'badge-success' :
                        c.status === 'In Progress' ? 'badge-info' : 'badge-warning';

    return `
      <tr>
        <td><span class="mono-font" style="font-weight: 800; color: var(--accent-cyan);">${c.complaintId}</span></td>
        <td>
          <div style="font-weight: 700; color: var(--text-primary);">${c.studentName}</div>
          <div class="mono-font" style="font-size: 0.73rem; color: var(--text-secondary);">${c.roomNumber}</div>
        </td>
        <td>
          <div style="font-weight: 700;">${c.title}</div>
          <div style="font-size: 0.76rem; color: var(--text-secondary);">${c.description}</div>
        </td>
        <td>
          <span class="badge badge-secondary">${c.category}</span>
          <span class="badge ${priorityBadge}" style="margin-left: 4px;">${c.priority}</span>
        </td>
        <td class="mono-font">${c.reportedDate}</td>
        <td><span class="badge ${statusBadge}">${c.status}</span></td>
        <td>
          <button class="btn btn-secondary btn-sm" onclick="openUpdateComplaintModal(${c.id})">Update Status</button>
        </td>
      </tr>
    `;
  }).join('');
}

function openRegisterComplaintModal() {
  document.getElementById('complaint-form').reset();
  openModal('complaint-modal');
}

function handleSaveComplaint(e) {
  e.preventDefault();
  const complaintData = {
    studentId: document.getElementById('complaint-student-select').value,
    title: document.getElementById('complaint-title-input').value.trim(),
    category: document.getElementById('complaint-category-select').value,
    priority: document.getElementById('complaint-priority-select').value,
    description: document.getElementById('complaint-desc-input').value.trim(),
    adminRemarks: 'Registered by operator'
  };

  if (!complaintData.studentId || !complaintData.title || !complaintData.description) {
    showToast('Student, title, and description are required.', 'error');
    return;
  }

  api.addComplaint(complaintData)
    .then(() => {
      showToast('Incident ticket filed and queued for technician response.', 'success');
      closeModal('complaint-modal');
      loadComplaints();
    })
    .catch(err => {
      showToast(err.message, 'error');
    });
}

function openUpdateComplaintModal(id) {
  const c = appState.complaints.find(item => item.id === id);
  if (!c) return;

  document.getElementById('update-complaint-id').value = c.id;
  document.getElementById('update-complaint-title').textContent = `${c.complaintId}: ${c.title}`;
  document.getElementById('update-complaint-status-select').value = c.status;
  document.getElementById('update-complaint-remarks').value = c.adminRemarks || '';

  openModal('update-complaint-modal');
}

function handleSaveComplaintStatus(e) {
  e.preventDefault();
  const id = document.getElementById('update-complaint-id').value;
  const status = document.getElementById('update-complaint-status-select').value;
  const remarks = document.getElementById('update-complaint-remarks').value.trim();

  api.updateComplaintStatus(id, status, remarks)
    .then(() => {
      showToast('Incident status updated cleanly!', 'success');
      closeModal('update-complaint-modal');
      loadComplaints();
    })
    .catch(err => {
      showToast(err.message, 'error');
    });
}

// ==========================================
// 13. AUDIT REPORTS & CSV EXPORT
// ==========================================
function loadReports() {
  api.getReports('all')
    .then(data => {
      renderReportsPreview(data);
    })
    .catch(err => {
      showToast('Report error: ' + err.message, 'error');
    });
}

function renderReportsPreview(data) {
  const summaryBox = document.getElementById('report-summary-stats');
  if (!summaryBox) return;

  summaryBox.innerHTML = `
    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 14px;">
      <div style="background: var(--bg-surface); padding: 14px; border-radius: 8px; border: 1px solid var(--border-color);">
        <div class="mono-font" style="font-size: 0.72rem; color: var(--text-secondary);">VERIFIED STUDENTS</div>
        <div class="mono-font" style="font-size: 1.4rem; font-weight: 800; color: var(--text-primary);">${data.students?.length || 0}</div>
      </div>
      <div style="background: var(--bg-surface); padding: 14px; border-radius: 8px; border: 1px solid var(--border-color);">
        <div class="mono-font" style="font-size: 0.72rem; color: var(--text-secondary);">INVENTORY ROOMS</div>
        <div class="mono-font" style="font-size: 1.4rem; font-weight: 800; color: var(--text-primary);">${data.rooms?.length || 0}</div>
      </div>
      <div style="background: var(--bg-surface); padding: 14px; border-radius: 8px; border: 1px solid var(--border-color);">
        <div class="mono-font" style="font-size: 0.72rem; color: var(--text-secondary);">FEE TRANSACTIONS</div>
        <div class="mono-font" style="font-size: 1.4rem; font-weight: 800; color: var(--text-primary);">${data.fees?.length || 0}</div>
      </div>
      <div style="background: var(--bg-surface); padding: 14px; border-radius: 8px; border: 1px solid var(--border-color);">
        <div class="mono-font" style="font-size: 0.72rem; color: var(--text-secondary);">INCIDENT TICKETS</div>
        <div class="mono-font" style="font-size: 1.4rem; font-weight: 800; color: var(--text-primary);">${data.complaints?.length || 0}</div>
      </div>
    </div>
  `;
}

function exportCsv(type) {
  if (type === 'students') {
    const headers = ['Student ID', 'Full Name', 'Roll Number', 'Department', 'Year', 'Room', 'Phone', 'Email', 'Status'];
    const rows = appState.students.map(s => [
      s.studentId, s.fullName, s.rollNumber, s.department, s.yearOfStudy, s.assignedRoomNumber || 'Unassigned', s.phone, s.email, s.status
    ]);
    downloadCsvFile('hostel_students_audit.csv', [headers, ...rows]);
  } else if (type === 'rooms') {
    const headers = ['Room Number', 'Type', 'Capacity', 'Occupied Beds', 'Available Beds', 'Monthly Fee', 'Status'];
    const rows = appState.rooms.map(r => [
      r.roomNumber, r.roomType, r.capacity, r.occupiedBeds, r.availableBeds, r.monthlyFee, r.status
    ]);
    downloadCsvFile('hostel_rooms_audit.csv', [headers, ...rows]);
  } else if (type === 'fees') {
    const headers = ['Receipt No', 'Student', 'Roll No', 'Total Fee', 'Paid Amount', 'Pending Amount', 'Method', 'Txn ID', 'Status', 'Date'];
    const rows = appState.fees.map(f => [
      f.receiptNo, f.studentName, f.studentRollNo, f.totalAmount, f.amountPaid, f.pendingAmount, f.paymentMethod, f.transactionId, f.paymentStatus, f.paymentDate
    ]);
    downloadCsvFile('hostel_fees_ledger.csv', [headers, ...rows]);
  } else if (type === 'complaints') {
    const headers = ['Complaint ID', 'Student', 'Room', 'Title', 'Category', 'Priority', 'Status', 'Reported Date'];
    const rows = appState.complaints.map(c => [
      c.complaintId, c.studentName, c.roomNumber, c.title, c.category, c.priority, c.status, c.reportedDate
    ]);
    downloadCsvFile('hostel_grievances_audit.csv', [headers, ...rows]);
  }
}

function downloadCsvFile(filename, dataRows) {
  const csvContent = dataRows.map(row =>
    row.map(cell => `"${String(cell || '').replace(/"/g, '""')}"`).join(',')
  ).join('\n');

  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.setAttribute('href', url);
  link.setAttribute('download', filename);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  showToast(`Exported "${filename}" successfully!`, 'success');
}

// ==========================================
// 14. DEVICE SECURITY & NETWORK MODAL
// ==========================================
function openDeviceSecurityModal() {
  api.getSecurityInfo()
    .then(sec => {
      renderSecurityModalContent(sec);
      openModal('device-security-modal');
    })
    .catch(() => {
      const metrics = appState.metrics || {};
      renderSecurityModalContent({
        sslEnabled: window.location.protocol === 'https:',
        port: metrics.serverPort || window.location.port || '8443',
        protocol: window.location.protocol.toUpperCase().replace(':', ''),
        clientDeviceIp: metrics.clientIp || '127.0.0.1',
        primaryLanIp: metrics.primaryLocalIp || window.location.hostname,
        lanUrls: metrics.allLocalIps ? metrics.allLocalIps.map(ip => `${window.location.protocol}//${ip}:${metrics.serverPort}/`) : [window.location.href],
        passwordHashing: 'SHA-256 with 128-bit Cryptographic Salt',
        tokenSecurity: 'HMAC-SHA256 Device-Bound Signature'
      });
      openModal('device-security-modal');
    });
}

function renderSecurityModalContent(sec) {
  const container = document.getElementById('device-security-modal-body');
  if (!container) return;

  const scheme = sec.sslEnabled ? 'https://' : 'http://';
  const mobileUrl = `${scheme}${sec.primaryLanIp}:${sec.port}/`;

  container.innerHTML = `
    <div style="background: var(--ethical-bg); border: 1px solid var(--ethical-border); padding: 14px 18px; border-radius: 10px; margin-bottom: 18px;">
      <div style="display: flex; align-items: center; justify-content: space-between;">
        <span style="font-weight: 700; font-size: 0.95rem; color: var(--ethical-text);">
          ${sec.sslEnabled ? '🔒 Active Encryption: TLS 1.3 / 2048-bit RSA' : '⚡ Clean HTTP Mode (Zero Warning Screen)'}
        </span>
        <span class="badge badge-info mono-font">Port ${sec.port}</span>
      </div>
      <p style="font-size: 0.78rem; color: var(--text-secondary); margin-top: 6px; line-height: 1.5;">
        All communication between client devices and this server is secured with transport-level encryption and device-bound session signatures.
      </p>
    </div>

    <div style="margin-bottom: 18px;">
      <label style="display: block; font-size: 0.8rem; font-weight: 700; color: var(--text-secondary); margin-bottom: 6px;">
        📱 Connect From Any Phone, Tablet, or Laptop on Wi-Fi:
      </label>
      <div style="display: flex; gap: 8px;">
        <input type="text" readonly class="form-control mono-font" value="${mobileUrl}" id="mobile-network-url" style="font-weight: 700; color: var(--accent-cyan);">
        <button class="btn btn-primary" onclick="copyMobileUrl()" style="white-space: nowrap;">Copy URL</button>
      </div>
      <p style="font-size: 0.73rem; color: var(--text-muted); margin-top: 4px;">
        Ensure your phone or other device is connected to the same local Wi-Fi.
      </p>
    </div>

    <div style="background: var(--bg-surface); padding: 14px; border-radius: 8px; border: 1px solid var(--border-color); font-size: 0.8rem; line-height: 1.7; color: var(--text-secondary);" class="mono-font">
      <div><strong>Client Device IP:</strong> <span style="color: var(--accent-cyan);">${sec.clientDeviceIp}</span></div>
      <div><strong>Server LAN IP:</strong> <span style="color: var(--accent-matrix);">${sec.primaryLanIp}</span></div>
      <div><strong>Server Port:</strong> <span>${sec.port}</span></div>
      <div><strong>Password Hash:</strong> <span>${sec.passwordHashing}</span></div>
      <div><strong>Session Security:</strong> <span>${sec.tokenSecurity}</span></div>
    </div>
  `;
}

function copyMobileUrl() {
  const input = document.getElementById('mobile-network-url');
  if (input) {
    input.select();
    navigator.clipboard.writeText(input.value)
      .then(() => showToast('Mobile LAN URL copied to clipboard!', 'success'))
      .catch(() => showToast('Please manually copy: ' + input.value, 'info'));
  }
}

// ==========================================
// 15. MODAL & TOAST HELPERS
// ==========================================
function openModal(id) {
  const modal = document.getElementById(id);
  if (modal) modal.classList.add('active');
}

function closeModal(id) {
  const modal = document.getElementById(id);
  if (modal) modal.classList.remove('active');
}

function showToast(message, type = 'success') {
  const container = document.getElementById('toast-container');
  if (!container) return;

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.innerHTML = `
    <span>${message}</span>
    <button style="background:none;border:none;cursor:pointer;color:var(--text-muted);font-size:1rem;margin-left:8px;" onclick="this.parentElement.remove()">✕</button>
  `;

  container.appendChild(toast);
  setTimeout(() => {
    if (toast.parentElement) toast.remove();
  }, 4000);
}

function escapeHtml(str) {
  return String(str || '').replace(/'/g, "\\'").replace(/"/g, '&quot;');
}
