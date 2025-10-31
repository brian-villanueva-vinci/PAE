import {awaitFront, showNavStyle} from "../../utils/function";
import {
  getAuthenticatedUser,
  getToken,
  setAuthenticatedUser
} from "../../utils/session";
import Navigate from "../../utils/Navigate";
import Navbar from "../Navbar/Navbar";

let companiesTable;

const closeForm = () => {
  const addCompanyContainer = document.querySelector(
      '.add-company-container');
  addCompanyContainer.classList.remove('slide-in');
  addCompanyContainer.classList.add('slide-out');
  const entrepriseBox = document.querySelector(".add-company-box");

  setTimeout(() => {
    entrepriseBox.style.visibility = "hidden";
    entrepriseBox.innerHTML = ``;
  }, 300);
}

const readAllCompanies = async () => {
  const user = await getAuthenticatedUser();
  setAuthenticatedUser(user);

  const options = {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': getToken()
    }
  }
  const response = await fetch('api/companies/all/user', options);

  if (!response.ok) {
    throw new Error(
        `fetch error : ${response.status} : ${response.statusText}`);
  }

  const companyInfo = await response.json();
  return companyInfo;
};

const submitRegistration = async (e) => {
  e.preventDefault();

  const user = await getAuthenticatedUser();
  setAuthenticatedUser(user);

  const name = document.querySelector("#input-name").value;
  const designation = document.querySelector("#input-designation").value;
  const address = document.querySelector("#input-adress").value;
  const phoneNumber = document.querySelector("#input-phone-number").value;
  const email = document.querySelector("#input-email").value;

  try {

    const options = {
      method: "POST", // *GET, POST, PUT, DELETE, etc.
      body: JSON.stringify({
        "name": name,
        "designation": designation,
        "address": address,
        "phoneNumber": phoneNumber,
        "email": email,
      }), // body data type must match "Content-Type" header
      headers: {
        "Content-Type": "application/json",
        "Authorization": getToken()
      },
    };

    const response = await fetch('api/companies/register', options);
    if (!response.ok) {
      throw new Error(
          `fetch error : ${response.status} : ${response.statusText}`
      );
    }

  } catch (error) {
    const errorMessage = document.getElementById("error-message");
    errorMessage.style.display = "block";

    if (error instanceof Error && error.message.startsWith(
        "fetch error : 400") && (!name || !address)) {
      errorMessage.innerText = "Le nom et l'addresse ne peuvent pas être vide";
    } else if (error instanceof Error && error.message.startsWith(
        "fetch error : 400") && (!phoneNumber && !email)) {
      errorMessage.innerText = "Veuillez entrer au moins un numéro de téléphone ou une adresse e-mail";
    }

    if (error instanceof Error && error.message.startsWith(
        "fetch error : 409")) {
      errorMessage.innerText = "Entreprise de même nom (et appellation) déjà existante, veuillez ajouter une nouvelle appellation";
    }

    if (error instanceof Error && error.message.startsWith(
        "fetch error : 500")) {
      errorMessage.innerText = "Une erreur interne s'est produite, veuillez réssayer";
    }
    return;
  }
  companiesTable = await readAllCompanies();
  showCompaniesList(companiesTable);
  await attachStartEvent();
  closeForm();
  // Navigate('/contact');
}

const renderRegisterCompanyForm = async () => {
  const entrepriseBox = document.querySelector(".add-company-box");
  entrepriseBox.innerHTML = `
    <div class="add-company-container d-flex justify-contain-center align-items-center flex-column mx-auto rounded-4">
      <div class="w-100 h-100 rounded-4 py-1" style="background: #119DB8">
        <div class="h-100 col-md-8 offset-md-2 rounded-1 py-3 px-5" style="background: white">
          <i id="company-back-btn" class="fa-solid fa-circle-arrow-left" title="Retour"></i>
          <div class="h-100 rounded-4 mx-5 d-flex flex-column justify-content-center align-items-center" style="border: 2px solid #119DB8">
            <h1 class="mb-5">Ajouter une entreprise</h1>
            <div class="input-group mb-3 w-75">
              <span class="input-group-text prepend-add-company rounded-start-5" id="basic-addon1"><i class="fa-solid fa-user"></i></span>
              <input type="text" class="form-control input-add-company rounded-end-5" id="input-name" placeholder="Nom" aria-label="Nom" aria-describedby="basic-addon1">
            </div>
            <div class="input-group mb-3 w-75">
              <span class="input-group-text prepend-add-company rounded-start-5" id="basic-addon1"><i class="fa-solid fa-user"></i></span>
              <input type="text" class="form-control input-add-company rounded-end-5" id="input-designation" placeholder="Appellation" aria-label="Appellation" aria-describedby="basic-addon1">
            </div>
            <div class="input-group mb-3 w-75">
              <span class="input-group-text prepend-add-company rounded-start-5" id="basic-addon1"><i class="fa-solid fa-user"></i></span>
              <input type="text" class="form-control input-add-company rounded-end-5" id="input-adress" placeholder="Adresse" aria-label="Adresse" aria-describedby="basic-addon1">
            </div>
            <div class="input-group mb-3 w-75">
              <span class="input-group-text prepend-add-company rounded-start-5" id="basic-addon1"><i class="fa-solid fa-phone"></i></span>
              <input type="text" class="form-control input-add-company rounded-end-5" id="input-phone-number" placeholder="Téléphone" aria-label="Téléphone" aria-describedby="basic-addon1">
            </div>
            <div class="input-group mb-3 w-75">
              <span class="input-group-text prepend-add-company rounded-start-5" id="basic-addon1"><i class="fa-solid fa-envelope"></i></span>
              <input type="text" class="form-control input-add-company rounded-end-5" id="input-email" placeholder="Adresse email" aria-label="Adresse email" aria-describedby="basic-addon1">
            </div>
            <!--<p class="btn-login" id="register-btn">Ajouter l'entreprise</p>-->
            <button class="register-company-btn rounded-1 px-2 py-3 w-50 mt-5">Ajouter l'entreprise</button>
            <h2 id="error-message"></h2>
          </div>
        </div>
      </div>
    </div>
  `;
  entrepriseBox.style.visibility = "visible";

  const addCompanyContainer = document.querySelector(
      '.add-company-container');
  addCompanyContainer.classList.add('slide-in');

  const btnBack = document.getElementById('company-back-btn');
  btnBack.addEventListener('click', () => {
    closeForm();
  });

  const registerBtn = document.querySelector('.register-company-btn');
  registerBtn.addEventListener('click', async (e) => {
    e.preventDefault();
    await submitRegistration(e);
  })

  const registerForm = document.querySelectorAll(
      '.add-company-container input');
  registerForm.forEach(input => {
    input.addEventListener('keypress', async (event) => {
      if (event.key === "Enter") {
        event.preventDefault();
        await submitRegistration(event);
      }
    });
  })

}

const ContactPage = async () => {

  const main = document.querySelector('main');
  awaitFront();

  const loggedUser = await getAuthenticatedUser();
  setAuthenticatedUser(loggedUser);
  Navbar();
  const userToken = getToken();
  if (!userToken) {
    Navigate('/');
    return;
  }

  companiesTable = await readAllCompanies();

  showNavStyle("contact");

  main.innerHTML = `
    <div class="container-fluid mt-5 d-flex flex-column rounded-3 overflow-hidden" style="width: 100%; height: 74vh; border: none; border-radius: 0; background: white; position: relative">
      <div class="row">
      
        <div class="col-md-6 offset-md-3 d-flex justify-content-center align-items-center input-group w-50">
          <input type="text" class="search-company form-control rounded-start-5 px-5 text-center" placeholder="Rechercher une entreprise" style="transform: none;">
          <span class="input-group-text prepend-add-company rounded-end-5" id="basic-addon1"><i class="fa-solid fa-search" style="font-size: 22px;"></i></span>
        </div>
        <div class="col-md-3 d-flex justify-content-end align-items-center" style="padding-right: 5rem">
          <button class="add-company-btn rounded-1 px-2 py-3">Ajouter une entreprise</button>
        </div>
        
        <div class="add-company-box w-100 h-100 d-flex justify-contain-center align-items-center" style="z-index: 1;">                    
        </div>
        
      </div>
      
      <div class="row mt-5 d-flex flex-column align-items-center">
        <div class="rounded-4 p-4 w-75" style="border: 2px solid #119cb8c7; height: 58vh;">
          <div class="col-md-12 d-flex flex-column h-100">
          
            <div class="users-container w-100 d-flex flex-column overflow-y-auto adminCompanyListTileContainer" style="scrollbar-width:none;">
              <h1>Il n'y a aucune entreprise</h1>
            </div>
            
          </div>
        </div>
      </div>
    </div>
  `;

  const searchBox = document.querySelector('.search-company');
  searchBox.addEventListener("input", (e) => {
    const value = e.target.value.toLowerCase();
    const filteredCompanies = companiesTable.filter(company => (
        (company.name && company.name.toLowerCase().includes(value)) ||
        (company.designation && company.designation.toLowerCase().includes(
            value)) ||
        (company.email && company.email.toLowerCase().includes(value)) ||
        (company.phoneNumber && company.phoneNumber.toLowerCase().includes(
            value))
    ));
    showCompaniesList(filteredCompanies);
  })

  const addCompanyBtn = document.querySelector('.add-company-btn');
  addCompanyBtn.addEventListener('click', () => {
    renderRegisterCompanyForm();
  })

  showCompaniesList(companiesTable);
  await attachStartEvent();

};

function showCompaniesList(companies) {
  const companiesContainer = document.querySelector('.users-container');
  companiesContainer.innerHTML = ``;

  let u = 0;
  let info = ``;
  while (u < companies.length) {
    let designation;
    let email;
    let phone;

    if (companies[u].designation === null || companies[u].designation
        === '') {
      designation = "Aucune appellation";
    } else {
      designation = companies[u].designation;
    }

    if (companies[u].email === null || companies[u].email === '') {
      email = "Aucun email";
    } else {
      email = companies[u].email;
    }

    if (companies[u].phoneNumber === null || companies[u].phoneNumber
        === '') {
      phone = "Aucun téléphone";
    } else {
      phone = companies[u].phoneNumber;
    }
    info += `
            <div data-${companies[u].id} class="w-100 d-flex justify-content-center align-items-center mt-2 py-2 border rounded-3 adminCompanyListTile" style="--orderCM:${u
    + 1}">
              <div class="d-flex align-items-center justify-content-center h-75" style="width: 25%; border-right: 2px solid white;">
                <p class="p-0 m-0 text-center">${companies[u].name}</p>
              </div>
              <div class="d-flex align-items-center justify-content-center h-75" style="width: 25%; border-right: 2px solid white;">
                <p class="p-0 m-0 text-center">${designation}</p>
              </div>
              <div class="d-flex align-items-center justify-content-center h-75" style="width: 20%; border-right: 2px solid white;">
                <p class="p-0 m-0 text-center">${email}</p>
              </div>
              <div class="d-flex align-items-center justify-content-center h-75" style="width: 20%; border-right: 2px solid white;">
                <p class="p-0 m-0 text-center">${phone}</p>
              </div>
            `;
    if (companies[u].blacklisted === false) {
      info += `
          <div class="d-flex align-items-center justify-content-center h-75" style="width: 10%;">
            <button id="${companies[u].id}" class="company-btn btn">Initier</button>
          </div>
        `;
    } else {
      info += `
          <div class="d-flex align-items-center justify-content-center" style="width: 10%;">
            <button id="${companies[u].id}" class="company-btn btn disabled">Initier</button>
          </div>
        `;
    }
    info += `</div>`;
    u += 1;
  }
  info += `<h2 id="error-message"></h2>`
  companiesContainer.innerHTML = info;
}

async function attachStartEvent() {
  const companiesBtn = document.querySelectorAll('.company-btn');

  if (companiesBtn) {
    companiesBtn.forEach(element => {
      element.addEventListener('click', async () => {
        if (await createContact(element.id)) {
          Navigate('/dashboard');
        }
      });
    });
  }
}

async function createContact(idCompany) {
  try {
    const user = await getAuthenticatedUser();
    const options = {
      method: 'POST',
      body: JSON.stringify({
        company: idCompany,
      }),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': user.token
      }
    }
    const response = await fetch('api/contacts/start', options);

    if (!response.ok) {
      throw new Error(
          `fetch error : ${response.status} : ${response.statusText}`);
    }

    const companyInfo = await response.json();
    return companyInfo;
  } catch (error) {
    const errorMessage = document.getElementById("error-message");
    errorMessage.style.display = "block";

    if (error instanceof Error && error.message.startsWith(
        "fetch error : 400")) {
      errorMessage.innerText = "Vous avez déjà un contact accepté.";
    }
    return undefined;
  }
}

export default ContactPage;