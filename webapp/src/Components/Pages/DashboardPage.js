import {awaitFront, showNavStyle} from "../../utils/function";

import {
  getAuthenticatedUser,
  getLocalUser,
  getToken,
  setAuthenticatedUser,
} from "../../utils/session";
import Navbar from "../Navbar/Navbar";
import {CreateInternshipPage} from "./InternshipPage";
import Navigate from "../../utils/Navigate";

const DashboardPage = async () => {

  const main = document.querySelector('main');
  awaitFront();

  let loggedUser = await getAuthenticatedUser();
  setAuthenticatedUser(loggedUser);
  Navbar();
  let userToken = getToken();
  let localUser = getLocalUser();
  if (!userToken) {
    Navigate('/');
    return;
  }

  /* const readUserInfo = async () => {
    const options = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': userToken
      }
    }
    const response = await fetch(`api/users/${localUser.id}`, options);

    if (!response.ok) {
      throw new Error(
          `fetch error : ${response.status} : ${response.statusText}`);
    }

    const userInfo = await response.json();
    return userInfo;
  }; */

  const readInternship = async () => {
    try {
      const options = {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': userToken
        }
      }
      const response = await fetch(`api/internships/student/${localUser.id}`,
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
    loggedUser = await getAuthenticatedUser();
    setAuthenticatedUser(loggedUser);
    userToken = getToken();
    localUser = getLocalUser();
    const options = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': userToken
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
          'Authorization': userToken
        }
      }

      const response = await fetch(`api/contacts/all/${localUser.id}`, options);

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

  // const userInfoID = await readUserInfo();
  const stageInfo = await readInternship();
  let contacts = await readAllContactsByStudent();

  showNavStyle("dashboard");

  main.innerHTML = `
        <div class="dash d-flex justify-content-center mt-5 mb-5 mx-auto">
            <div class="dash-left d-flex align-items-center flex-column ms-3 me-3 h-100">
                <div class="dash-year d-flex justify-content-center align-items-center flex-column">
                    <i class="fa-solid fa-calendar-days mt-3"></i>
                    <p class="mt-2">${localUser.schoolYear}</p>
                </div>
                <div class="dash-info mt-4 d-flex justify-content-center align-items-center flex-column">   
                    <i class="fa-solid fa-circle-info"></i>
                    <h1 class="mt-2 mb-5">Informations<br>personnelles</h1>
                    <p>${localUser.email}</p>
                    <p>${localUser.firstname}</p>
                    <p>${localUser.lastname}</p>
                    <p>${localUser.phoneNumber}</p>
                    <span id="btn-info-change" class="mt-4">Changer mes informations</span>
                </div>
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
                                <div class="title-col-4 mt-3">
                                  <p>Année académique</p>
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
    `;

  const stageBox = document.querySelector('.dash-stage');

  if (stageInfo) {
    let {designation} = stageInfo.contact.company;
    const {address, name} = stageInfo.contact.company;
    let {project} = stageInfo;
    let {email} = stageInfo.supervisor;
    const {lastname, firstname, phoneNumber} = stageInfo.supervisor;

    if (designation === null) {
      designation = "";
    }
    if (project === null) {
      project = "";
    }
    if (email === null) {
      email = "";
    }
    const dateSignatureSql = stageInfo.signatureDate.substring(0, 10);
    const dateSignature = new Date(dateSignatureSql);
    dateSignature.setDate(dateSignature.getDate() + 1);
    stageBox.innerHTML = `        
          <div class="stage-bloc">
              <h1 class="mb-3">Votre stage</h1>
              <div class="d-flex">
                  <p class="me-4"><i class="fa-solid fa-signature"></i> ${name} ${designation}</p>
                  <p><i class="fa-solid fa-location-dot"></i> ${address}</p>
              </div>
              <div class="d-flex flex-wrap">
                <p class="me-4"><i class="fa-solid fa-list"></i> ${project}</p>
                <p class="me-4"><i class="fa fa-calendar-check-o"></i> ${dateSignature.getFullYear()}-${(dateSignature.getMonth()+1).toString().padStart(2, '0')}-${(dateSignature.getDate()).toString().padStart(2, '0')}</p>
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

  const tableContacts = document.querySelector(".table-line-box");
  const boxInfo = document.querySelector('.entreprise-box');

  showContacts(contacts);

  const btnChangeInfo = document.getElementById("btn-info-change");

  if (btnChangeInfo) {
    btnChangeInfo.addEventListener('click', () => {
      Navigate('/info');
    });
  }

  function showContacts(contactsTable) {
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
                <div class="table-line d-flex align-items-center mt-2 mb-2 rounded-3" style="--orderCT:${u}">
                    <div class="d-flex justify-content-center align-items-center position-relative" style="width: 60%;">
                      <i class="line-info fa-solid fa-circle-info position-absolute" style="left: 0;" id="${contactsTable[u].id}"></i>
                      <div class="line-col-1" >
                          <p class="mx-auto mt-3" style="color: #119DB8">${contactsTable[u].company.name}<br>${designation}</p>
                      </div>
                    </div>
                    <div class="line-col-4">
                      <p>${contactsTable[u].schoolYear}</p>
                    </div>
                    
                    <div class="line-col-2 d-flex flex-column align-items-center justify-content-center" style="width: 20%;">
                      <p class="m-0 rounded-1 py-1 w-50 ${stateColor}">${contactsTable[u].state}</p>
                    </div>
                    
                    <div class="${contactsTable[u].state === 'pris' ? 'd-block'
          : 'd-none'} line-col-3">
                      <button data-id="${contactsTable[u].id}" class="accept-contact-btn rounded-1 px-0 py-2 w-50">Accepter</button>
                    </div>
                </div>
            `;
      u += 1;
    }
    tableContacts.innerHTML = info;
    const acceptBtns = document.querySelectorAll('.accept-contact-btn');
    acceptBtns.forEach(btn => {
      btn.addEventListener('click', (e) => {
        const contactId = e.currentTarget.getAttribute('data-id');
        const contactInfo = contacts.find(
            contact => contact.id === parseInt(contactId, 10));
        CreateInternshipPage(contactInfo)
      });
    })
    clickContactInfo();
  }

  function clickContactInfo() {
    const allContactsBtn = document.querySelectorAll(".line-info");
    allContactsBtn.forEach(element => {
      element.addEventListener('click', (e) => {
        e.preventDefault();
        conctactInfo(element.id);
      });
    });
  }

  async function conctactInfo(id) {
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

    const disableRadioButtons = meetingType !== null ? 'disabled' : '';
    const disableTextarea = refusal !== "" ? 'disabled' : '';

    const init = `
      <option value="admitted">pris</option>
      <option value="unsupervised">ne plus suivre</option>
    `;
    const admit = `
    <option value="turnedDown">refusé</option>
    <option value="unsupervised">ne plus suivre</option>
    `;

    entrepriseBox.innerHTML = `
                    <div class="entreprise-container d-flex justify-contain-center align-items-center flex-column mx-auto">
                        <i id="btn-back2" class="fa-solid fa-circle-arrow-left" title="Retour"></i>
                        <h1 class="mt-3">${contactInfoJSON.company.name}</h1>
                        <div class="entreprise-info overflow-y-scroll" style="scrollbar-width:none">
                            <p class="mt-3"><i class="fa-solid fa-phone"></i><i id="phoneNumber"></i></p>
                            <p class="mt-1"><i class="fa-solid fa-map-location-dot"></i><i id="address"></i></p>
                        
                            <div class="d-flex mt-2">
                                <p class="fw-bold me-4" style="width: 30%;">Etat</p>
                                <select id="selectedState" class="form-select" aria-label="Default select example" ${contactInfoJSON.state
    === 'initié' || contactInfoJSON.state === 'pris' ? '' : 'disabled'}>
                                    <option value="basic" selected>${contactInfoJSON.state}</option>
                                    <!--<option value="started">initié</option>-->
                                    ${contactInfoJSON.state === 'initié' ? init
        : ''}
                                    ${contactInfoJSON.state === 'pris' ? admit
        : ''}
                                    <!--<option value="onHold">suspendu</option>-->
                                </select>
                            </div>
                            
                            <div class="radioButton d-flex mt-4 align-items-center admit-extra" style="visibility: hidden; height: 0">
                                <p class="fw-bold me-4" style="width: 30%;">Type de rencontre</p>
                                <div class="ent-radio form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="inlineRadioOptions" id="inlineRadio1" value="Dans l entreprise" ${checkedSurPlace} ${disableRadioButtons}>
                                    <label class="form-check-label" for="inlineRadio1">Dans l'entreprise</label>
                                </div>
                                <div class="ent-radio form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="inlineRadioOptions" id="inlineRadio2" value="A distance" ${checkedADistance} ${disableRadioButtons}>
                                    <label class="form-check-label" for="inlineRadio2">A Distance</label>
                                </div>
                            </div>
                            
                            <div class="d-flex mt-2 mb-2 refused-extra align-items-center" style="visibility: hidden; height: 0;"> 
                                <p class="fw-bold me-4 mb-0" style="width: 30%;">Raison du refus</p>
                                <textarea id="refusalReason" class="px-3 pt-4 rounded-1" name="raison" placeholder="Raison du refus" ${disableTextarea}>${refusal}</textarea>
                            </div>
                            
                            <div class="d-flex justify-content-center">
                              <button id="updateBtn" class="btn btn-primary mt-1 mb-2 ms-3 ${
        contactInfoJSON.state === 'initié' ||
        contactInfoJSON.state === 'pris' ? 'visible' : 'invisible'}" type="submit">Mettre à jour</button>
                            </div>
                            
                            <h2 id="error-message" class="mt-0"></h2>
                        </div>
                    </div>
        `

    const selectState = document.querySelector('#selectedState');
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

    selectState.addEventListener('change', () => {
      if (meetingType === null) {
        admitExtra.style.visibility = 'hidden';
        admitExtra.style.height = '0';
      }
      if (refusal === "") {
        refusedExtra.style.visibility = 'hidden';
        refusedExtra.style.height = '0';
      }
      if (selectState.value === 'admitted' && meetingType === null) {
        admitExtra.style.visibility = 'visible';
        admitExtra.style.height = 'auto';
      } else if (selectState.value === 'turnedDown' && refusal === "") {
        refusedExtra.style.visibility = 'visible';
        refusedExtra.style.height = 'auto';
      }
    })

    let phone;
    let address;
    const {address: address1, phoneNumber} = contactInfoJSON.company;
    if (phoneNumber === null) {
      phone = "";
    } else {
      phone = phoneNumber;
    }
    if (address1 === null) {
      address = "";
    } else {
      address = address1;
    }

    document.getElementById("phoneNumber").innerHTML = phone;
    document.getElementById("address").innerHTML = address;

    const updateState = document.getElementById("updateBtn");
    updateState.addEventListener('click', async (e) => {
      e.preventDefault();
      const user = await getAuthenticatedUser();
      setAuthenticatedUser(user);
      const options = {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': user.token
        }
      }
      const newState = document.getElementById("selectedState").value;
      let meeting;
      const refusalReason = document.getElementById("refusalReason").value;
      const roleRadioBtn = document.querySelectorAll(
          '.radioButton input[type="radio"]');
      roleRadioBtn.forEach(button => {
        if (button.checked) {
          meeting = button.value;
        }
      });

      const errorMessage = document.getElementById("error-message");
      switch (newState) {
        case "admitted":
          options.body = JSON.stringify({
            "contactId": id,
            "meeting": meeting,
            "version": contactInfoJSON.version
          });
          try {
            const response = await fetch("/api/contacts/admit", options);
            if (!response.ok) {
              throw new Error(
                  `fetch error : ${response.status} : ${response.statusText}`
              );
            }
          } catch (error) {
            if (error instanceof Error && error.message.startsWith(
                "fetch error : 400")) {
              errorMessage.innerHTML = "Veuillez renseigner un type de rencontre correct, où vérifier que vous pouvez mettre le nouvel état."
              errorMessage.style.display = "block";
              return;
            }
            if (error instanceof Error && error.message.startsWith(
                "fetch error : 403")) {
              errorMessage.innerHTML = "Vous ne pouvez pas changer ce contact."
              errorMessage.style.display = "block";
              return;
            }
            errorMessage.innerHTML = "Erreur interne, veuillez réessayer."
            errorMessage.style.display = "block";
            return;
          }
          // Navigate("/dashboard");
          break;
        case "turnedDown":
          options.body = JSON.stringify(
              {
                contactId: id,
                reasonForRefusal: refusalReason,
                "version": contactInfoJSON.version
              });
          try {
            const response = await fetch("/api/contacts/turnDown", options);
            if (!response.ok) {
              throw new Error(
                  `fetch error : ${response.status} : ${response.statusText}`
              );
            }
          } catch (error) {
            if (error instanceof Error && error.message.startsWith(
                "fetch error : 400")) {
              errorMessage.innerHTML = "Veuillez entrer la raison du refus.";
              errorMessage.style.display = "block";
              return;
            }
            if (error instanceof Error && error.message.startsWith(
                "fetch error : 403")) {
              errorMessage.innerHTML = "Vous ne pouvez pas refusé un contact non pris.";
              errorMessage.style.display = "block";
              return;
            }
            if (error instanceof Error && error.message.startsWith(
                "fetch error : 404")) {
              errorMessage.innerHTML = "Veuillez entrer un contact existant.";
              errorMessage.style.display = "block";
              return;
            }
            errorMessage.innerHTML = "Erreur interne, veuillez réessayer."
            errorMessage.style.display = "block";
            return;
          }
          // Navigate("/dashboard");
          break;

        case "unsupervised":
          options.body = JSON.stringify(
              {contactId: id, "version": contactInfoJSON.version});
          try {
            const response = await fetch("/api/contacts/unsupervise", options);
            if (!response.ok) {
              throw new Error(
                  `fetch error : ${response.status} : ${response.statusText}`
              );
            }
          } catch (error) {
            if (error instanceof Error && error.message.startsWith(
                "fetch error : 400")) {
              errorMessage.innerHTML = "Veuillez entrer un contact ou vérifiez que vous pouvez effectuer ce changement.";
              errorMessage.style.display = "block";
              return;
            }
            if (error instanceof Error && error.message.startsWith(
                "fetch error : 403")) {
              errorMessage.innerHTML = "Vous n'avez pas les droits ou vous essayez d'arrêter de suivre un contact non initié ou non pris.";
              errorMessage.style.display = "block";
              return;
            }
            if (error instanceof Error && error.message.startsWith(
                "fetch error : 404")) {
              errorMessage.innerHTML = "Veuillez entrer un contact existant.";
              errorMessage.style.display = "block";
              return;
            }
            errorMessage.innerHTML = "Erreur interne, veuillez réessayer."
            errorMessage.style.display = "block";
            return;
          }
          // Navigate("/dashboard");
          break;
        default:
          errorMessage.innerHTML = "Veuillez entrer un contact ou vérifiez que vous pouvez effectuer ce changement.";
          errorMessage.style.display = "block";
          return;
      }
      contacts = await readAllContactsByStudent();
      showContacts(contacts);
      closeBox();
    });

    const entrepriseContainer = document.querySelector('.entreprise-container');
    entrepriseContainer.classList.add('fade-in');
    boxInfo.style.visibility = "visible";

    const btnBack = document.getElementById('btn-back2');
    btnBack.addEventListener('click', () => {

      entrepriseContainer.classList.remove('fade-in');
      entrepriseContainer.classList.add('fade-out');

      setTimeout(() => {
        boxInfo.style.visibility = "hidden";
        boxInfo.innerHTML = ``;
      }, 150)

    });

    function closeBox() {
      boxInfo.style.visibility = "hidden";
      boxInfo.innerHTML = ``;
    }
  }
};

export default DashboardPage;