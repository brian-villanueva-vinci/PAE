import TomSelect from "tom-select";

import {awaitFront, showNavStyle} from "../../utils/function";
import {
  getAuthenticatedUser,
  getLocalUser,
  getToken,
  setAuthenticatedUser
} from "../../utils/session";
import Navbar from "../Navbar/Navbar";
import Navigate from "../../utils/Navigate";
import StudentPage from "./StudentPage";

const attachRenderStudentEvent = () => {
  const studentTiles = document.querySelectorAll('[data-student]');
  studentTiles.forEach(studentTile => {
    studentTile.addEventListener('click', async () => {
      setTimeout(() => {
        const loadContainer = document.querySelector(".load-container");
        loadContainer.style.visibility = "visible";
      }, 350);

      await StudentPage(studentTile.dataset.student);
    })
  });
}

const UserListPage = async () => {

  const main = document.querySelector('main');

  awaitFront();

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

  showNavStyle("userList");

  const readAllUsers = async () => {
    const options = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': userAuth.token
      }
    }
    const response = await fetch('api/users/all', options);

    if (!response.ok) {
      throw new Error(
          `fetch error : ${response.status} : ${response.statusText}`);
    }

    const userInfo = await response.json();
    return userInfo;
  };

  const readAllInternships = async () => {
    const options = {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': userAuth.token
      }
    }
    const response = await fetch('api/internships/all', options);

    if (!response.ok) {
      throw new Error(
          `fetch error : ${response.status} : ${response.statusText}`);
    }

    const userInfo = await response.json();
    return userInfo;
  };

  const users = await readAllUsers();
  const interships = await readAllInternships();

  main.innerHTML = `
    <div class="d-flex align-items-center flex-column mt-5 mb-0 pb-5 position-relative" style="height: 74vh;">
    
      <div class="search-criteria d-flex justify-content-center align-items-center mb-5">
        <select id="year-input" class="me-3">
          <option selected>2023-2024</option>
        </select> 
        <select id="search-input" class="search-css me-3" placeholder="Recherchez..." multiple></select>
        <div class="form-check student-input d-flex justify-content-center align-items-center">
          <input class="form-check-input" type="checkbox" id="filter-students">
          <label class="form-check-label" for="filter-students">Utilisateurs</label>
        </div>
      </div>
      
      <div class="users-table d-flex flex-column align-items-center">
              
      </div>
      
      <div class="d-flex justify-content-center align-items-center w-100 load-container position-absolute" style="visibility: hidden; background: white; z-index: 2;">
        <div class="await d-flex justify-content-center align-items-center">
            <i class="fa-solid fa-circle"></i>
            <i class="fa-solid fa-circle"></i>
            <i class="fa-solid fa-circle"></i>
        </div>
      </div>
      
      <div class="add-company-box w-100 d-flex align-items-center student-page-container overflow-y-scroll overflow-x-hidden" style="background: white; z-index: 2; height: 96%; scrollbar-width:none;">
      
      </div>
      
    </div>
  `;

  const userTable = document.querySelector('.users-table');
  const selectElement = document.getElementById('search-input');
  let checkBox = false;
  const labelCheck = document.querySelector(".form-check-label");
  const yearsInput = document.getElementById("year-input");

  // eslint-disable-next-line no-unused-vars
  const tomSelectInstance = new TomSelect(selectElement, {
    plugins: ['remove_button'],
    valueField: 'idUser',
    labelField: 'fullName',
    searchField: ['fullName'],
    load(query, callback) {
      // Filtrer les utilisateurs dont le prénom contient le texte de recherche
      const utilisateursFiltres = users.filter(utilisateur => {
        const flname = `${utilisateur.firstname} ${utilisateur.lastname}`;
        return flname.toLowerCase().includes(query.toLowerCase());

      });

      const idsUtilisateursFiltres = utilisateursFiltres.map(
          utilisateur => utilisateur.id);
      showUsers(idsUtilisateursFiltres);
      callback(idsUtilisateursFiltres);
    },
    onChange: showUsers,
    render: {
      no_results() {
        return '<div class="no-results">Aucun utilisateur trouvé</div>';
      }
    },
  });

  tomSelectInstance.addOption(users.map(user => ({
    idUser: user.id,
    fullName: `${user.firstname} ${user.lastname}`,
  })));

  const yearsTable = [];
  let o = 0;
  let p = 0;
  let yearCheck = false;
  let yearValue;
  let yearAdd = ``;

  while (o < users.length) {
    yearValue = users[o].schoolYear;
    p = 0;
    yearCheck = false;
    while (p < yearsTable.length) {
      if (yearsTable[p] === users[o].schoolYear) {
        yearCheck = true;
      }
      p += 1;
    }

    if (yearCheck === false) {
      yearsTable.push(yearValue);
    }

    o += 1;
  }

  p = 0;

  function comparerDates(a, b) {
    // eslint-disable-next-line
    const anneeDebutA = parseInt(a.split('-')[0]);
    // eslint-disable-next-line
    const anneeDebutB = parseInt(b.split('-')[0]);

    return anneeDebutA - anneeDebutB;
  }

  yearsTable.sort(comparerDates);

  while (p < yearsTable.length) {

    if (yearsTable[p] === "2023-2024") {
      yearAdd += `<option value="${yearsTable[p]}" selected>${yearsTable[p]}</option>`;
    } else {
      yearAdd += `<option value="${yearsTable[p]}">${yearsTable[p]}</option>`;
    }
    p += 1;
  }

  yearAdd += `<option value="all">Toutes</option>`;
  yearsInput.innerHTML = yearAdd;

  allUsers();
  attachRenderStudentEvent();

  document.querySelector('.form-check-label').addEventListener('click', () => {
    if (checkBox === false) {
      checkBox = true;
      labelCheck.innerHTML = `Etudiants`;
    } else {
      checkBox = false;
      labelCheck.innerHTML = `Utilisateurs`;
    }
    showUsers(tomSelectInstance.items);
  });

  yearsInput.addEventListener('change', () => {
    showUsers(tomSelectInstance.items);
  });

  function showUsers(selectedIds) {

    let userLine = ``;
    if (selectedIds.length <= 0) {
      tomSelectInstance.clear();
      allUsers();
    } else {
      // eslint-disable-next-line
      for (let i = 0; i < selectedIds.length; i++) {
        const currentUser = users[selectedIds[i] - 1];
        if (
            (yearsInput.value === "all" || currentUser.schoolYear
                === yearsInput.value) &&
            (checkBox === true && currentUser.role === "Etudiant" || checkBox
                === false)
        ) {
          userLine += generateUserHTML(currentUser);
        }
      }
      userTable.innerHTML = userLine;
    }
    attachRenderStudentEvent();
  }

  function allUsers() {
    let info = ``;
    let u = 0;

    while (u < users.length) {
      if ((yearsInput.value === "all" || users[u].schoolYear
              === yearsInput.value) &&
          (checkBox === true && users[u].role === "Etudiant" || checkBox
              === false)) {
        info += generateUserHTML(users[u]);
      }
      u += 1;
    }
    userTable.innerHTML = info;
  }

  function generateUserHTML(userTemp) {

    if (userTemp.role === "Etudiant") {
      let k = 0;
      let findStageB = false;

      while (k < interships.length) {
        if (interships[k].contact.student.id === userTemp.id) {
          findStageB = true;
        }
        k += 1;
      }

      const findStage = (findStageB === true) ? 'A trouvé<br>un stage'
          : 'N\'a pas trouvé<br>de stage';
      return `
          <div data-student="${userTemp.id}" class="user-line d-flex align-items-center user-line-student">
              <i class="users-icon fa-solid fa-user"></i>
              <h1>${userTemp.firstname}<br>${userTemp.lastname}</h1>
              <div class="user-email-tel d-flex justify-content-center flex-column">
                  <p><i class="fa-solid fa-envelope"></i> ${userTemp.email}</p>
                  <p><i class="fa-solid fa-phone"></i> ${userTemp.phoneNumber}</p>
              </div>
              <h3>${userTemp.schoolYear}</h3>
              <h4>${findStage}</h4>
              <h2>${userTemp.role}</h2>
          </div>
      `;
      // eslint-disable-next-line
    } else {
      return `
          <div class="user-line d-flex align-items-center">
              <i class="users-icon fa-solid fa-user"></i>
              <h1>${userTemp.firstname}<br>${userTemp.lastname}</h1>
              <div class="user-email-tel d-flex justify-content-center flex-column">
                  <p><i class="fa-solid fa-envelope"></i> ${userTemp.email}</p>
                  <p><i class="fa-solid fa-phone"></i> ${userTemp.phoneNumber}</p>
              </div>
              <h3>${userTemp.schoolYear}</h3>
              <h2>${userTemp.role}</h2>
          </div>
      `;
    }
  }

};

export default UserListPage;
