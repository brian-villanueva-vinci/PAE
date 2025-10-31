import {
  getAuthenticatedUser,
  getLocalUser,
  getToken,
  setAuthenticatedUser
} from "../../utils/session";
import Navigate from "../../utils/Navigate";
import Navbar from "../Navbar/Navbar";

let studentId;

const closeForm = () => {
  const addCompanyContainer = document.querySelector(
      '.add-company-container');
  addCompanyContainer.classList.remove('zoom-in');
  addCompanyContainer.classList.add('zoom-out');
  const entrepriseBox = document.querySelector(".add-company-box");

  setTimeout(() => {
    entrepriseBox.style.visibility = "hidden";
    entrepriseBox.innerHTML = ``;
  }, 300);
}

const readUserInfo = async () => {
  const options = {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': getToken()
    }
  }
  const response = await fetch(`api/users/${studentId}`, options);

  if (!response.ok) {
    throw new Error(
        `fetch error : ${response.status} : ${response.statusText}`);
  }

  const userInfo = await response.json();
  return userInfo;
};

const readInternship = async () => {
  try {
    const options = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': getToken()
      }
    }
    const response = await fetch(`api/internships/student/${studentId}`,
        options);

    if (!response.ok) {
      throw new Error(
          `fetch error : ${response.status} : ${response.statusText}`);
    }

    const userInfo = await response.json();
    if (userInfo) {
      return userInfo;
    }
    return null;
  } catch (error) {
    if (error instanceof Error && error.message.startsWith(
        "fetch error : 500")) {
      return null;
    }
    if (error instanceof Error && error.message.startsWith(
        "fetch error : 404")) {
      return null;
    }
    return null;
  }
};

const readContactById = async (idContact) => {
  const options = {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': getToken()
    }
  }
  const response = await fetch(`api/contacts/${idContact}`, options);

  if (!response.ok) {
    throw new Error(
        `fetch error : ${response.status} : ${response.statusText}`);
  }

  const userInfo = await response.json();
  return userInfo;
};

const readAllContactsByStudent = async () => {
  try {
    const options = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': getToken()
      }
    }

    const response = await fetch(`api/contacts/all/${studentId}`, options);

    if (!response.ok) {
      if (response.status === 401) {
        Navigate("/");
      }
      throw new Error(
          `fetch error : ${response.status} : ${response.statusText}`);
    }
    const contactList = await response.json();
    if (contactList) {
      return contactList;
    }
    return null;
  } catch (error) {
    return null;
  }
};

const conctactInfo = async (id) => {
  const entrepriseBox = document.querySelector(".entreprise-box");
  const contactInfoJSON = await readContactById(id);
  const meetingType = contactInfoJSON.meeting;

  const checkedSurPlace = meetingType === "Dans l entreprise" ? 'checked'
      : '';
  const checkedADistance = meetingType === "A distance" ? 'checked' : '';

  let refusal;
  if (!contactInfoJSON.reasonRefusal) {
    refusal = "";
  } else {
    refusal = contactInfoJSON.reasonRefusal;
  }

  entrepriseBox.innerHTML = `
                    <div class="entreprise-container d-flex justify-contain-center align-items-center flex-column mx-auto">
                        <i id="btn-back2" class="fa-solid fa-circle-arrow-left" title="Retour"></i>
                        <h1 class="mt-3">${contactInfoJSON.company.name}</h1>
                        <div class="entreprise-info overflow-y-scroll" style="scrollbar-width:none">
                            <p class="mt-3"><i class="fa-solid fa-phone"></i><i id="phoneNumber">${contactInfoJSON.company.phoneNumber
      ? contactInfoJSON.company.phoneNumber : ''}</i></p>
                            <p class="mt-1"><i class="fa-solid fa-map-location-dot"></i><i id="address">${contactInfoJSON.company.address
      ? contactInfoJSON.company.address : ''}</i></p>
                        
                            <div class="d-flex mt-2">
                                <p class="fw-bold me-4" style="width: 30%;">Etat</p>
                                <select id="selectedState" class="form-select" aria-label="Default select example" disabled>
                                    <option value="basic" selected>${contactInfoJSON.state}</option>
                                </select>
                            </div>
                            
                            <div class="radioButton d-flex mt-4 align-items-center admit-extra" style="visibility: hidden; height: 0">
                                <p class="fw-bold me-4" style="width: 30%;">Type de rencontre</p>
                                <div class="ent-radio form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="inlineRadioOptions" id="inlineRadio1" value="Dans l entreprise" ${checkedSurPlace} disabled>
                                    <label class="form-check-label" for="inlineRadio1">Dans l'entreprise</label>
                                </div>
                                <div class="ent-radio form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="inlineRadioOptions" id="inlineRadio2" value="A distance" ${checkedADistance} disabled>
                                    <label class="form-check-label" for="inlineRadio2">A Distance</label>
                                </div>
                            </div>
                            
                            <div class="d-flex mt-2 mb-2 refused-extra align-items-center" style="visibility: hidden; height: 0;"> 
                                <p class="fw-bold me-4 mb-0" style="width: 30%;">Raison du refus</p>
                                <textarea id="refusalReason" class="px-3 pt-4" name="raison" placeholder="Raison du refus" disabled>${refusal}</textarea>
                            </div>
                            
                            <div class="d-flex justify-content-center">
                              <button id="updateBtn" class="btn btn-secondary mt-1 mb-2 ms-3 ${
      contactInfoJSON.state === 'initié' ||
      contactInfoJSON.state === 'pris' ? 'visible' : 'invisible'}" type="submit" disabled>Mettre à jour</button>
                            </div>
                            
                            <h2 id="error-message" class="mt-2"></h2>
                        </div>
                    </div>
        `

  const admitExtra = document.querySelector('.admit-extra');
  const refusedExtra = document.querySelector('.refused-extra');

  if (meetingType !== null) {
    admitExtra.style.visibility = 'visible';
    admitExtra.style.height = 'auto';
  }
  if (refusal !== "") {
    refusedExtra.style.visibility = 'visible';
    refusedExtra.style.height = 'auto';
  }

  const entrepriseContainer = document.querySelector('.entreprise-container');
  entrepriseContainer.classList.add('fade-in');
  entrepriseBox.style.visibility = "visible";

  const btnBack = document.getElementById('btn-back2');
  btnBack.addEventListener('click', () => {

    entrepriseContainer.classList.remove('fade-in');
    entrepriseContainer.classList.add('fade-out');

    setTimeout(() => {
      entrepriseBox.style.visibility = "hidden";
      entrepriseBox.innerHTML = ``;
    }, 150);

  });
}

const attachInfoEvent = () => {
  const allContactsBtn = document.querySelectorAll(".line-info");
  allContactsBtn.forEach(element => {
    element.addEventListener('click', async (e) => {
      e.preventDefault();
      await conctactInfo(element.id);
    });
  });
}

const renderContactList = (contactsTable) => {
  const tableContacts = document.querySelector(".table-line-box");
  if (!contactsTable) {
    return;
  }
  tableContacts.innerHTML = ``;

  let u = 0;
  let info = ``;
  while (u < contactsTable.length) {
    let designation;
    if (contactsTable[u].company.designation === null) {
      designation = "";
    } else {
      designation = contactsTable[u].company.designation;
    }

    let stateColor = '';
    switch (contactsTable[u].state) {
      case "non suivi":
        stateColor = "greyout"
        break;
      case "suspendu":
        stateColor = "greyout"
        break;
      case "refusé":
        stateColor = "redout"
        break;
      case "initié":
        stateColor = "lightblueout"
        break;
      case "pris":
        stateColor = "blueout"
        break;
      case "accepté":
        stateColor = "greenout"
        break;
      default:
        stateColor = "greyout"
    }

    info += `
                <div class="table-line d-flex align-items-center mt-2 mb-2 rounded-3" style="--orderCT:${u};">
                    <div class="d-flex justify-content-center align-items-center position-relative" style="width: 60%;">
                      <i class="line-info fa-solid fa-circle-info position-absolute" style="left: 0;" id="${contactsTable[u].id}"></i>
                      <div class="line-col-1" >
                          <p class="mx-auto mt-3">${contactsTable[u].company.name}<br>${designation}</p>
                      </div>
                    </div>
                    
                    <div class="line-col-2 d-flex flex-column align-items-center justify-content-center" style="width: 20%;">
                      <p class="m-0 rounded-1 py-1 w-50 ${stateColor}">${contactsTable[u].state}</p>
                    </div>
                    
                    <div class="${contactsTable[u].state === 'pris' ? 'd-block'
        : 'd-none'}" style="width: 20%;">
                      <button data-id="${contactsTable[u].id}" class="accept-contact-btn rounded-1 px-0 py-2 w-50 bg-secondary" style="animation: none;" disabled>Accepter</button>
                    </div>
                </div>
            `;
    u += 1;
  }
  tableContacts.innerHTML = info;
  attachInfoEvent();
}

const renderInternshipInfo = (stageObj) => {
  const stageBox = document.querySelector('.dash-stage');
  if (stageObj) {
    let {designation} = stageObj.contact.company;
    const {address, name} = stageObj.contact.company;
    let {project} = stageObj;
    let {email} = stageObj.supervisor;
    const {lastname, firstname, phoneNumber} = stageObj.supervisor;

    if (designation === null) {
      designation = "";
    }
    if (project === null) {
      project = "";
    }
    if (email === null) {
      email = "";
    }

    stageBox.innerHTML = `        
          <div class="stage-bloc">
              <h1 class="mb-3">Votre stage</h1>
              <div class="d-flex">
                  <p class="me-4"><i class="fa-solid fa-signature"></i> ${name} ${designation}</p>
                  <p><i class="fa-solid fa-location-dot"></i> ${address}</p>
              </div>
              <div class="d-flex flex-wrap">
                <p class="me-4"><i class="fa-solid fa-list"></i> ${project}</p>
                <p class="me-4"><i class="fa fa-calendar-check-o"></i> ${stageObj.signatureDate}</p>
              </div>
          </div>
          <div class="respo-bloc p-1" style="position: relative">
              <div class="w-100 h-100 py-3 px-4" style="background: #119DB8; border-radius: 8px;">
                  <h1 class="mt-0">Votre responsable</h1>
                  <p class="mt-2 mb-0"><i class="fa-solid fa-user me-3"></i> ${firstname} ${lastname}</p>
                  <div class="d-flex flex-wrap">
                    <span class="mt-2 me-3"><i class="fa-solid fa-phone"></i>${phoneNumber}</span>
                    <span class="mt-2 me-3"><i class="fa-solid fa-at"></i>${email}</span>
                  </div>
              </div>
          </div>
      `;
  } else {
    stageBox.innerHTML = `        
          <div class="stage-bloc">
                <h1 class="mt-3">Vous n'avez pas de stage</h1>
          </div>
      `;
  }
}

const renderStudentInfo = (studentObj) => {
  const studentInfoSide = document.querySelector('.dash-left');
  studentInfoSide.innerHTML = `
    <div class="dash-year d-flex justify-content-center align-items-center flex-column">
        <i class="fa-solid fa-calendar-days mt-3"></i>
        <p class="mt-2">${studentObj.schoolYear}</p>
    </div>
    <div class="dash-info mt-4 d-flex justify-content-center align-items-center flex-column">   
        <i class="fa-solid fa-circle-info"></i>
        <h1 class="mt-2 mb-5">Informations<br>personnelles</h1>
        <p>${studentObj.email}</p>
        <p>${studentObj.firstname}</p>
        <p>${studentObj.lastname}</p>
        <p>${studentObj.phoneNumber}</p>
    </div>
  `;
}

const StudentPage = async (student) => {

  studentId = student;

  const userAuth = await getAuthenticatedUser();
  setAuthenticatedUser(userAuth);
  Navbar();
  const localUser = getLocalUser();
  if (!getToken() || !localUser) {
    Navigate('/');
    return;
  }
  if (localUser.role !== 'Professeur' && localUser.role
      !== 'Administratif') {
    Navigate('/dashboard');
    return;
  }

  const studentInfo = await readUserInfo();
  const internshipInfo = await readInternship();
  const contactsInfo = await readAllContactsByStudent();

  const studentBox = document.querySelector(".add-company-box");
  studentBox.innerHTML = `
    <div class="add-company-container d-flex justify-contain-center align-items-center flex-column mx-auto w-100 h-100">
          <i id="student-back-btn" class="fa-solid fa-times-circle" title="Retour" style="z-index: 4;"></i>
          <div class="dash d-flex justify-content-center align-items-center mt-0 mb-5 mx-auto">
            <div class="dash-left d-flex align-items-center flex-column ms-3 me-3 h-100">
                
            </div>
            <div class="dash-right d-flex justify-content-center align-items-center flex-column ms-3 me-3">
                <div class="dash-stage d-flex justify-content-center align-items-center py-1 px-3 overflow-x-hidden">
                    
                </div>
                <div class="dash-en-container mt-4 d-flex justify-content-center align-items-center overflow-hidden">
                    <div class="dash-en d-flex align-items-center flex-column pb-3">
                        <div class="table-title d-flex justify-content-center align-items-center mt-3 font-weight-bold">
                                <div class="title-col-1 mt-3">
                                    <p>Nom</p>
                                </div>
                                <div class="title-col-2 mt-3">
                                    <p>Etat</p>
                                </div>
                                <div class="title-col-3 mt-3">
                                    <p>Action</p>
                                </div>
                        </div>
                        <div class="table-line-box overflow-auto mt-1" style="scrollbar-width:none;">
                            
                        </div>
                    </div>
                    <div class="entreprise-box d-flex justify-contain-center align-items-center">
                        
                    </div>
                </div>
            </div>
            
        </div>
    </div>
  `;

  renderStudentInfo(studentInfo);
  renderInternshipInfo(internshipInfo);
  renderContactList(contactsInfo);

  studentBox.style.visibility = "visible";

  const addCompanyContainer = document.querySelector(
      '.add-company-container');
  addCompanyContainer.classList.add('zoom-in');

  const btnBack = document.getElementById('student-back-btn');
  btnBack.addEventListener('click', () => {
    closeForm();
  });

  const loadContainer = document.querySelector(".load-container");
  loadContainer.style.visibility = "hidden";

};

export default StudentPage;