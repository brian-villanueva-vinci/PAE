import {awaitFront, showNavStyle} from "../../utils/function";
import {
  getAuthenticatedUser,
  getLocalUser,
  getToken,
  setAuthenticatedUser
} from "../../utils/session";
import BlacklistPage from "./BlacklistPage";
import Navigate from "../../utils/Navigate";
import Navbar from "../Navbar/Navbar";

let dataCompany = [];
let filteredData = [];
let sortStates = {};
let clickTimer = null;

const fetchInternshipStat = async () => {
  try {
    const user = getToken();
    if (!user) {
      throw new Error("fetch error : 403");
    }

    const response = await fetch('api/internships/stats/year', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': user
      }
    });

    if (!response.ok) {
      throw new Error(
          `fetch error : ${response.status} : ${response.statusText}`
      );
    }

    return response.json();
  } catch (error) {
    if (error instanceof Error && error.message.startsWith(
        "fetch error : 403")) {
      Navigate('/');
    }
    return null;
  }
}

const fetchCompaniesData = async () => {
  const user = getToken();
  try {
    const response = await fetch('api/companies/all', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': user
      }
    });

    if (!response.ok) {
      throw new Error(
          `fetch error : ${response.status} : ${response.statusText}`
      );
    }
    const companiesData = await response.json();
    dataCompany = companiesData;
    return companiesData;
  } catch (error) {
    if (error instanceof Error && error.message.startsWith(
        "fetch error : 403")) {
      Navigate('/');
    }
    return null;
  }
}

const fetchCompanyContactData = async (companyId) => {
  const userToken = getToken();
  try {
    const response = await fetch(`api/contacts/all/company/${companyId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': userToken
      }
    });

    if (!response.ok) {
      throw new Error(
          `fetch error : ${response.status} : ${response.statusText}`
      );
    }

    return response.json();
  } catch (error) {
    if (error instanceof Error && error.message.startsWith(
        "fetch error : 403")) {
      Navigate('/');
    }
    return null;
  }
}

const closeForm = () => {
  const addCompanyContainer = document.querySelector(
      '.contact-company-list');
  addCompanyContainer.classList.remove('slide-in');
  addCompanyContainer.classList.add('slide-out');
  const entrepriseBox = document.querySelector(".contact-company-container");

  setTimeout(() => {
    entrepriseBox.style.visibility = "hidden";
    entrepriseBox.innerHTML = ``;
  }, 300);
}

const sortData = (data, sortingType) => {
  const sortOrder = sortStates[sortingType];
  const sortedData = Object.values(data).sort((a, b) => {
    if (sortingType === 'isBlacklisted') {
      return sortOrder === 'asc' ? a[sortingType] - b[sortingType]
          : b[sortingType] - a[sortingType];
    }
    if (sortingType === 'data') {
      const valueA = Object.values(a[sortingType])[0];
      const valueB = Object.values(b[sortingType])[0];
      return sortOrder === 'asc' ? valueA - valueB : valueB - valueA;
    }
    const valueA = a[sortingType] ? a[sortingType].toLowerCase() : '';
    const valueB = b[sortingType] ? b[sortingType].toLowerCase() : '';

    if (sortingType === 'name' && valueA === valueB) {
      const designationA = a.designation ? a.designation.toLowerCase() : '';
      const designationB = b.designation ? b.designation.toLowerCase() : '';
      return designationA.localeCompare(designationB);
    }
    return sortOrder === 'asc' ? valueA.localeCompare(valueB)
        : valueB.localeCompare(valueA);
  });
  sortStates[sortingType] = sortOrder === 'asc' ? 'desc' : 'asc';
  return sortedData;
}

const hideTooltip = () => {
  document.getElementById('tooltip').style.display = 'none';
}

const drawPieChart = (canvas, dataSet, colors) => {

  // Check percentage & colors having same length
  if (dataSet.length !== colors.length) {
    return;
  }

  // Get 2D context from canvas
  const ctx = canvas.getContext('2d');

  // Center of circle
  const centerX = canvas.width / 2;
  const centerY = canvas.height / 2;

  // Circle radius (get min from half width & height)
  const radius = Math.min(centerX, centerY);

  // Total percentage
  const totalValue = dataSet.reduce(
      (total, currentValue) => total + currentValue, 0);

  // Start angle for each slice
  let startAngle = 0;

  // Draw slice
  const drawSlice = (currentAngle, endAngle, color, duration) => {
    const startTime = performance.now();
    const endTime = startTime + duration;
    const draw = () => {
      const now = performance.now();
      const progress = Math.min((now - startTime) / duration, 1);
      const angle = currentAngle + (endAngle - currentAngle) * progress;

      ctx.fillStyle = color;
      ctx.beginPath();
      ctx.moveTo(centerX, centerY);
      ctx.arc(centerX, centerY, radius, currentAngle, angle);
      ctx.closePath();
      ctx.fill();

      if (now < endTime) {
        requestAnimationFrame(draw);
      }
    };
    requestAnimationFrame(draw);
  };

  // Draw each slice of pie chart
  dataSet.forEach((percentage, index) => {
    // Calc angle for each slice
    const sliceAngle = (percentage / totalValue) * 2 * Math.PI;

    // Draw slice
    drawSlice(startAngle, startAngle + sliceAngle, colors[index],
        250);

    // Update starting angle for next slice
    startAngle += sliceAngle;
  });

  const tooltip = document.querySelector('#tooltip');

  canvas.addEventListener('mousemove', (event) => {
    const rect = canvas.getBoundingClientRect();
    const mouseX = event.clientX - rect.left;
    const mouseY = event.clientY - rect.top;

    const distanceFromCenter = Math.sqrt(
        (mouseX - centerX) ** 2 + (mouseY - centerY) ** 2);

    if (distanceFromCenter <= radius) {
      // Calc angle from hovered slice
      const angle = Math.atan2(mouseY - centerY, mouseX - centerX);
      const normalizedAngle = (angle < 0) ? (2 * Math.PI + angle) : angle;

      // Find hovered slice index
      let hoveredSliceIndex = -1;
      let cumulativeAngle = 0;
      for (let i = 0; i < dataSet.length; i += 1) {
        const sliceAngle = (dataSet[i] / totalValue) * 2 * Math.PI;
        if (normalizedAngle >= cumulativeAngle && normalizedAngle
            <= cumulativeAngle + sliceAngle) {
          hoveredSliceIndex = i;
          break;
        }
        cumulativeAngle += sliceAngle;
      }

      // If hovered slice, show tooltip
      if (hoveredSliceIndex !== -1) {
        const percentage = (dataSet[hoveredSliceIndex] / totalValue)
            * 100;
        // Show tooltip next to cursor
        tooltip.textContent = `${percentage.toFixed(2)}%`;
        tooltip.style.left = `${event.pageX + 10}px`;
        tooltip.style.top = `${event.pageY - 20}px`;
        tooltip.style.display = 'block';
      }
    }
  });

  canvas.addEventListener('mouseleave', () => {
    hideTooltip();
  })

}

const renderChart = (internshipStats) => {

  const chartContainer = document.querySelector('.chartCT');
  const percent = (internshipStats.internshipCount
      / internshipStats.totalStudents) * 100;

  chartContainer.innerHTML = `
    <canvas class="myChart" width="164" height="164"></canvas>
  `;
  const chartCanvas = document.querySelector('.myChart');
  drawPieChart(chartCanvas, [percent, 100 - percent], ['#119DB8', 'white']);

}

const renderCaption = (internshipStats) => {

  const caption = document.querySelector('.stat-caption');
  caption.innerHTML = `
    <p class="mt-3 mb-0">Total : ${internshipStats.totalStudents} étudiants</p>
    <p class="mt-0 mb-3">Ont un stage : ${internshipStats.internshipCount} étudiants</p>
    <div class="rounded-5 mb-2 cap-1 px-2">
      <p class="m-0">Pas de stage</p>
    </div>
    <div class="rounded-5 mb-3 cap-2 px-2">
      <p class="m-0">Ont un stage</p>
    </div>
  `;

}

const renderContactBox = async (company, contactDataList) => {
  const contactBox = document.querySelector('.contact-company-container');
  contactBox.innerHTML = `
    <div class="contact-company-list w-100 d-flex flex-column align-items-center py-3">
      <div class="row w-100 py-3">
        <div class="col-md-4 d-flex flex-column justify-content-center align-items-start">
          <i id="contact-company-back-btn" class="fa-solid fa-circle-arrow-left" title="Retour" style="margin-left: 10%;"></i>
        </div>
        <div class="col-md-4 d-flex flex-column justify-content-center align-items-center text-center px-2 py-3 rounded-3" style="color: white; background: #119DB8;">
          <p class="mb-0 h3">${company.name}<br>${company.designation
      ? company.designation : ''}</p>
        </div>
        <div class="col-md-4 d-flex flex-column justify-content-center align-items-end">
          ${company.isBlacklisted === false
      ? '<button class="blacklist-company-btn rounded-1 px-2 py-3 w-50" style="margin-right: 10%;">Blacklister</button>'
      : ""}
        </div>
      </div>
      
      <div class="row w-100 py-3 d-flex ${company.isBlacklisted === true
      ? 'd-block' : 'd-none'} rounded-3" style="background: #A10E31; color: white; border: 2px solid black;">
        <div class="d-flex flex-column align-items-center">
          <p class="p-0 m-0 text-center h5">Motivation du Blacklist: </p>
          <p class="p-0 m-0 text-center">${company.blacklistMotivation}</p>
        </div>
      </div>
      
      <div class="contact-company-tile-list w-100 d-flex flex-column align-items-center overflow-y-auto" style="scrollbar-width:none;">
        <div class="w-100 d-flex align-items-center justify-content-center rounded-3 border mt-3 py-3 adminCompanyListTile">
          <div class="d-flex align-items-center justify-content-center" style="width: 25%; border-right: 2px solid white;">
            <p class="p-0 m-0 text-center">Caroline Line</p>
          </div>
          <div class="d-flex align-items-center justify-content-center" style="width: 10%; border-right: 2px solid white;">
            <p class="p-0 m-0 text-center">2023-2024</p>
          </div>
          <div class="d-flex align-items-center justify-content-center" style="width: 15%; border-right: 2px solid white;">
            <p class="p-0 m-0 text-center">Dans l entreprise</p>
          </div>
          <div class="d-flex align-items-center justify-content-center" style="width: 40%; border-right: 2px solid white;">
            <p class="p-0 m-0 text-center">Raison de refus, la y'en a pas jsp quoi mettre qsdlhqsd qskjhsdq qskjhsqd qsdkjhqsd</p>
          </div>
          <div class="d-flex align-items-center justify-content-center" style="width: 10%">
            <p class="p-2 m-0 text-center rounded-3" style="color: #119DB8; background: white">non suivi</p>
          </div>
        </div>
      </div>
      
    </div>
  `;

  const contactTileListBox = document.querySelector(
      '.contact-company-tile-list');
  contactTileListBox.innerHTML = Object.values(contactDataList).map(
      (contact, index) =>
          `
        <div class="w-100 d-flex align-items-center justify-content-center rounded-3 border mt-3 py-2 adminCompanyListTile" style="--orderCM:${index};">
          <div class="d-flex align-items-center justify-content-center" style="width: 25%; border-right: 2px solid white;">
            <p class="p-0 m-0 text-center">${contact.student.firstname} ${contact.student.lastname}</p>
          </div>
          <div class="d-flex align-items-center justify-content-center" style="width: 10%; border-right: 2px solid white;">
            <p class="p-0 m-0 text-center">${contact.schoolYear}</p>
          </div>
          <div class="d-flex align-items-center justify-content-center" style="width: 15%; border-right: 2px solid white;">
            <p class="p-0 m-0 text-center">${contact.meeting ? contact.meeting
              : '-'}</p>
          </div>
          <div class="d-flex align-items-center justify-content-center" style="width: 40%; border-right: 2px solid white;">
            <p class="p-0 m-0 text-center">${contact.reasonRefusal
              ? contact.reasonRefusal : '-'}</p>
          </div>
          <div class="d-flex align-items-center justify-content-center" style="width: 10%">
            <p class="p-2 m-0 text-center rounded-3 w-75" style="color: #119DB8; background: white">${contact.state}</p>
          </div>
        </div>
      `
  ).join('');

  contactBox.style.visibility = "visible";

  const contactCompanyContainer = document.querySelector(
      '.contact-company-list');
  contactCompanyContainer.classList.add('slide-in');

  const btnBack = document.getElementById('contact-company-back-btn');
  btnBack.addEventListener('click', () => {
    closeForm();
  });

  if (!company.isBlacklisted) {
    const blacklistBtn = document.querySelector('.blacklist-company-btn');
    blacklistBtn.addEventListener('click', async (e) => {
      e.preventDefault();
      await BlacklistPage(company.id);
    })
  }
}

const renderCompanyList = (companyData) => {
  const listContainer = document.querySelector(
      '.adminCompanyListTileContainer');
  const selectedYear = document.querySelector('.year-list').value;

  listContainer.innerHTML = Object.values(companyData).map((data, index) => {
    let dataValue;
    if (selectedYear !== "Par défaut") {
      dataValue = data.data[selectedYear];
    } else {
      dataValue = Object.values(data.data).reduce((acc, curr) => acc + curr, 0);
    }
    return `
      <div data-id="${data.id}" class="w-100 d-flex align-items-center justify-content-center rounded-3 border mt-3 py-3 adminCompanyListTile adminCompanyListHover" style="--orderCM:${index}; opacity: 1;">
        <div class="d-flex align-items-center justify-content-center" style="width: 30%; border-right: 2px solid white;">
          <p class="p-0 m-0 text-center">${data.name}</p>
        </div>
        <div class="d-flex align-items-center justify-content-center" style="width: 30%; border-right: 2px solid white;">
          <p class="p-0 m-0 text-center">${data.designation ? data.designation
        : '-'}</p>
        </div>
        <div class="d-flex align-items-center justify-content-center" style="width: 20%; border-right: 2px solid white;">
          <p class="p-0 m-0 text-center">${data.phoneNumber}</p>
        </div>
        <div class="d-flex align-items-center justify-content-center" style="width: 10%; border-right: 2px solid white;">
          <p class="p-0 m-0 text-center">${dataValue === undefined ? 0
        : dataValue}</p>
        </div>
        <div class="d-flex align-items-center justify-content-center" style="width: 10%">
          <p class="p-0 m-0 text-center">${data.isBlacklisted ? "OUI" : "NON"}</p>
        </div>
      </div>
    `;
  }).join('');
  const companyTiles = document.querySelectorAll('.adminCompanyListTile');
  companyTiles.forEach(companyTile => {
    companyTile.addEventListener('click', async () => {
      const chooseCompany = dataCompany[companyTile.dataset.id];
      const contactData = await fetchCompanyContactData(chooseCompany.id);
      await renderContactBox(chooseCompany, contactData);
    })
  })
}

const addColumnHeaderListeners = () => {
  const headers = document.querySelectorAll('[data-sort]');
  headers.forEach(header => {
    header.addEventListener('click', () => {
      const paragraph = header.querySelector('p');
      let sortingType;
      switch (paragraph.textContent.trim()) {
        case 'Nom':
          sortingType = 'name';
          break;
        case 'Appellation':
          sortingType = 'designation';
          break;
        case 'Numéro de téléphone':
          sortingType = 'phoneNumber';
          break;
        case 'Pris en stage':
          sortingType = 'data';
          break;
        case 'Black-listé':
          sortingType = 'isBlacklisted';
          break;
        default:
          sortingType = 'name';
      }
      clearTimeout(clickTimer);

      clickTimer = setTimeout(() => {
        const sortedData = sortData(filteredData, sortingType);
        renderCompanyList(sortedData);
      }, 200);
    });
  });
}

const renderYearOptions = (internshipStats, companiesData) => {
  const selectYear = document.querySelector('.year-list');
  const years = Object.keys(internshipStats);

  years.unshift("Par défaut");

  selectYear.innerHTML = years.map(
      year => `<option value="${year}">${year}</option>`).join(
      '');

  selectYear.addEventListener('change', (e) => {
    const selectedYear = e.target.value;
    let selectedStats;

    if (selectedYear === "Par défaut") {
      selectedStats = internshipStats[years[1]];
      filteredData = Object.values(companiesData).map(company => {
        const totalData = Object.values(company.data).reduce(
            (acc, curr) => acc + curr, 0);
        return {
          ...company,
          data: {[selectedYear]: totalData}
        };
      });
    } else {
      selectedStats = internshipStats[selectedYear];
      filteredData = Object.values(companiesData).map(company => {
        const filteredDataForYear = company.data[selectedYear];
        return {
          ...company,
          data: filteredDataForYear ? {[selectedYear]: filteredDataForYear} : {}
        };
      }).filter(company => Object.keys(company.data).length > 0);
    }

    renderChart(selectedStats);
    renderCaption(selectedStats);
    hideTooltip();

    renderCompanyList(filteredData);
    addColumnHeaderListeners();
  });

  selectYear.dispatchEvent(new Event('change'));
}

const AdminDashboardPage = async () => {

  const loggedUser = await getAuthenticatedUser();
  setAuthenticatedUser(loggedUser);
  Navbar();
  const userToken = getToken();
  const localUser = getLocalUser();
  if (!userToken || !localUser) {
    Navigate('/');
    return;
  }
  if (localUser.role !== 'Professeur' && localUser.role
      !== 'Administratif') {
    Navigate('/dashboard');
    return;
  }

  const main = document.querySelector('main');
  awaitFront();

  const internshipStats = await fetchInternshipStat();
  dataCompany = await fetchCompaniesData();

  showNavStyle("dashboard");

  sortStates = {
    name: 'asc',
    designation: 'asc',
    phoneNumber: 'asc',
    data: 'asc',
    blacklist: 'asc'
  };

  const sortDataCompany = sortData(dataCompany, 'name');

  main.innerHTML = `
    <div class="container-fluid justify-content-center align-items-center mt-5 mb-5 mx-auto" style="border: none; height: 69vh;">
      <div class="row mx-2">
        <div class="col-md-3">
          <div class="card chart-card dash-row rounded-4">
            <div class="card-body d-flex flex-column align-items-center justify-content-around py-4">
            
              <div class="d-flex flex-column align-items-center w-50">
                <p class="mb-0 mb-2">Année académique</p>
                <select class="year-list custom-select-options rounded-1 text-center mb-4 py-1 border-0 w-100">
                  <option selected>2023-2024</option>
                </select>
              </div>
              
              <div class="chart-container w-75 mb-4 d-flex justify-content-center align-items-center" style="width: 100%; height: 200px;">
                <div class="chart-container" >
                  <div class="chartCT d-flex justify-content-center align-items-center">
                    <canvas class="myChart"></canvas>
                  </div>
                </div>
              </div>
              
              <div class="stat-caption w-75 d-flex flex-column align-items-center rounded-3">
                <p class="mt-3 mb-3">Total : 115 étudiants</p>
                <div class="rounded-5 mb-2 cap-1 px-2">
                  <p class="m-0">Pas de stage</p>
                </div>
                <div class="rounded-5 mb-3 cap-2 px-2">
                  <p class="m-0">Ont un stage</p>
                </div>
              </div>
              
            </div>
          </div>
        </div>
        <div class="col-md-9">
          <div class="dash-row">
            <div class="rounded-4 dash-row p-4 overflow-hidden" style="border: 2px solid #119cb8c7; margin-left: 4rem; position: relative;">
            
              <div class="col-md-12 d-flex flex-column h-100 overflow-hidden">
              
                <div class="w-100 d-flex justify-content-center align-items-center border adminCompanyListTitle">
                  <div data-sort class="sort-header d-flex align-items-center justify-content-center position-relative" style="width: 30%">
                      <p class="p-2 m-0 text-center">Nom</p>
                      <div class="d-flex flex-column position-absolute" style="right: 10%;">
                        <span class="triangle-up h-25" style="font-size: 10px; color: white;">&#9650;</span>
                        <span class="triangle-down h-25" style="font-size: 10px; color: white;">&#9660;</span>
                      </div>
                  </div>
                  <div data-sort class="sort-header d-flex align-items-center justify-content-center position-relative" style="width: 30%">
                      <p class="p-2 m-0 text-center">Appellation</p>
                      <div class="d-flex flex-column position-absolute" style="right: 10%;">
                        <span class="triangle-up h-25" style="font-size: 10px; color: white;">&#9650;</span>
                        <span class="triangle-down h-25" style="font-size: 10px; color: white;">&#9660;</span>
                      </div>
                  </div>
                  <div data-sort class="sort-header d-flex align-items-center justify-content-center position-relative" style="width: 20%">
                      <p class="p-2 m-0 text-center">Numéro de téléphone</p>
                      <div class="d-flex flex-column position-absolute" style="right: 10%;">
                        <span class="triangle-up h-25" style="font-size: 10px; color: white;">&#9650;</span>
                        <span class="triangle-down h-25" style="font-size: 10px; color: white;">&#9660;</span>
                      </div>
                  </div>
                  <div data-sort class="sort-header d-flex align-items-center justify-content-center position-relative" style="width: 10%">
                      <p class="p-2 m-0 text-center">Pris en stage</p>
                      <div class="d-flex flex-column position-absolute" style="right: 10%;">
                        <span class="triangle-up h-25" style="font-size: 10px; color: white;">&#9650;</span>
                        <span class="triangle-down h-25" style="font-size: 10px; color: white;">&#9660;</span>
                      </div>
                  </div>
                  <div data-sort class="sort-header d-flex align-items-center justify-content-center position-relative" style="width: 10%">
                      <p class="p-2 m-0 text-center">Black-listé</p>
                      <div class="d-flex flex-column position-absolute" style="right: 10%;">
                        <span class="triangle-up h-25" style="font-size: 10px; color: white;">&#9650;</span>
                        <span class="triangle-down h-25" style="font-size: 10px; color: white;">&#9660;</span>
                      </div>
                  </div>
                </div>
                
                <div class="w-100 d-flex flex-column overflow-y-auto adminCompanyListTileContainer" style="scrollbar-width:none;">
                  
                </div>
                
              </div>
              
              <div class="contact-company-container w-100 h-100 d-flex justify-contain-center p-4 rounded-4" style="z-index: 1;">
              
              </div>
              
            </div>
          </div>
        </div>
      </div>
    </div>
  `;

  // Create tooltip
  const tooltip = document.createElement('div');
  tooltip.id = 'tooltip';
  tooltip.style.position = 'absolute';
  tooltip.style.display = 'none';
  tooltip.style.background = '#E6C060';
  tooltip.style.color = '#fff';
  tooltip.style.padding = '5px';
  tooltip.style.borderRadius = '5px';
  document.body.appendChild(tooltip);

  renderYearOptions(internshipStats, dataCompany);
  renderChart(internshipStats[Object.keys(internshipStats)[0]]);
  renderCaption(internshipStats[Object.keys(internshipStats)[0]]);

  renderCompanyList(sortDataCompany);
  addColumnHeaderListeners();

}

export default AdminDashboardPage;