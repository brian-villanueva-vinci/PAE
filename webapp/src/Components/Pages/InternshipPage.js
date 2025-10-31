import TomSelect from "tom-select";

import {awaitFront, showNavStyle} from "../../utils/function";
import {
  getAuthenticatedUser,
  getToken,
  setAuthenticatedUser,
} from "../../utils/session";
import Navigate from "../../utils/Navigate";
import Navbar from "../Navbar/Navbar";

const InternshipPage = async () => {
  const main = document.querySelector('main');
  awaitFront();

  const user = await getAuthenticatedUser();
  setAuthenticatedUser(user);
  Navbar();
  const userToken = getToken();
  if (!userToken) {
    Navigate('/');
    return;
  }

  showNavStyle("internship");

  const readInternship = async () => {
    try {
      const options = {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': user.token
        }
      }
      const response = await fetch(`api/internships/student/${user.user.id}`,
          options);

      if (!response.ok) {
        throw new Error(
            `fetch error : ${response.status} : ${response.statusText}`);
      }

      const internship = await response.json();
      if (internship) {
        main.innerHTML = "";
        return internship;
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

  const internship = await readInternship();
  if (!internship) {
    main.innerHTML = `
      <div class="dash d-flex justify-content-center align-items-center mt-5 mb-5 mx-auto">
        <div class="dash-content d-flex justify-content-center align-items-center flex-column">
          <div class="dash-info mt-4 d-flex justify-content-center align-items-center flex-column">
              <h1 class="noInternship mt-2 mb-5">Vous n'avez pas de stage cette année.</h1>
              <span id="btn-info-change" class="btn btn-primary mt-4">Voir mes contacts</span>
          </div>
        </div>
      </div>
    `
    const btnChangeInfo = document.getElementById("btn-info-change");
    btnChangeInfo.addEventListener('click', () => {
      Navigate('/dashboard');
    });
  } else {
    const divMaster = document.createElement("div");
    divMaster.className = "row justify-content-center my-4 display-flex";

    const divInternshipMother = document.createElement("div");
    divInternshipMother.className = "box-internshipMother justify-content-center ";
    const divInternshipChild = document.createElement("div");
    divInternshipChild.className = "box-internshipChild row justify-content-center my-4 display-flex";
    const divLeft = document.createElement("div");
    divLeft.className = "row justify-content-center my-4 display-flex col-md-6";
    const divRight = document.createElement("div");
    divRight.className = "row justify-content-center my-4 display-flex col-md-6";

    // title
    const title = document.createElement("h1");
    title.innerHTML = `Votre stage : ${internship.schoolYear}`;
    title.className = "text-center textCSS"
    divInternshipChild.appendChild(title);

    // company name
    const labelCompanyName = document.createElement("label");
    labelCompanyName.innerText = "Entreprise :";
    labelCompanyName.className = "textCSS";
    const valueLabelCompanyName = document.createElement("p");
    valueLabelCompanyName.innerText = `${internship.contact.company.name}`;
    valueLabelCompanyName.className = "valueLabel textCSS";
    labelCompanyName.appendChild(valueLabelCompanyName);
    divLeft.appendChild(labelCompanyName);
    divLeft.appendChild(document.createElement("br"))

    // company designation
    if (internship.contact.company.designation) {
      const labelCompanyDesignation = document.createElement("label");
      labelCompanyDesignation.innerText = "Appellation :";
      labelCompanyDesignation.className = "textCSS";
      const valueLabelCompanyDesignation = document.createElement("p");
      valueLabelCompanyDesignation.innerText = `${internship.contact.company.designation}`;
      valueLabelCompanyDesignation.className = "valueLabel textCSS";
      labelCompanyDesignation.appendChild(valueLabelCompanyDesignation);
      divLeft.appendChild(labelCompanyDesignation);
      divLeft.appendChild(document.createElement("br"));
    }

    // company address
    const labelCompanyAddress = document.createElement("label");
    labelCompanyAddress.innerText = "Adresse :";
    labelCompanyAddress.className = "textCSS";
    const valueLabelCompanyAddress = document.createElement("p");
    valueLabelCompanyAddress.innerText = `${internship.contact.company.address}`;
    valueLabelCompanyAddress.className = "valueLabel textCSS";
    labelCompanyAddress.appendChild(valueLabelCompanyAddress);
    divLeft.appendChild(labelCompanyAddress);
    divLeft.appendChild(document.createElement("br"))

    // company phone number
    if (internship.contact.company.phoneNumber) {
      const labelCompanyPhoneNumber = document.createElement("label");
      labelCompanyPhoneNumber.innerText = "Téléphone :";
      labelCompanyPhoneNumber.className = "textCSS";
      const valueLabelCompanyPhoneNumber = document.createElement("p");
      valueLabelCompanyPhoneNumber.innerText = `${internship.contact.company.phoneNumber}`;
      valueLabelCompanyPhoneNumber.className = "valueLabel textCSS";
      labelCompanyPhoneNumber.appendChild(valueLabelCompanyPhoneNumber);
      divLeft.appendChild(labelCompanyPhoneNumber);
      divLeft.appendChild(document.createElement("br"));
    }

    // company email
    if (internship.contact.company.email) {
      const labelCompanyEmail = document.createElement("label");
      labelCompanyEmail.innerText = "Email :";
      labelCompanyEmail.className = "textCSS";
      const valueLabelCompanyEmail = document.createElement("p");
      valueLabelCompanyEmail.innerText = `${internship.contact.company.email}`;
      valueLabelCompanyEmail.className = "valueLabel textCSS";
      labelCompanyEmail.appendChild(valueLabelCompanyEmail);
      divLeft.appendChild(labelCompanyEmail);
      divLeft.appendChild(document.createElement("br"));
    }

    // internship project
    const labelInternshipProject = document.createElement("label");
    labelInternshipProject.innerText = "Sujet du stage :";
    labelInternshipProject.className = "textCSS";
    const valueLabelInternshipProject = document.createElement("p");
    const newValueLabelInternshipProject = document.createElement('input');
    if (internship.project) {
      valueLabelInternshipProject.innerHTML = `${internship.project}`;
      newValueLabelInternshipProject.value = `${internship.project}`;
    } else {
      valueLabelInternshipProject.innerHTML = `Pas encore de sujet de stage`;
      newValueLabelInternshipProject.value = ``;
    }
    const iconEditProject = document.createElement('i');
    iconEditProject.className = "fa-solid fa-pencil";
    iconEditProject.addEventListener("click", () => {
      labelInternshipProject.innerHTML = "";
      labelInternshipProject.innerText = "Sujet du stage :";
      newValueLabelInternshipProject.className = "newValueLabel textCSS";
      const submitButton = document.createElement("i");
      submitButton.className = "submitProjectButton fa-solid fa-check";
      labelInternshipProject.appendChild(newValueLabelInternshipProject);
      labelInternshipProject.appendChild(submitButton);
      const errorMessage = document.createElement("p");
      labelInternshipProject.appendChild(errorMessage);

      submitButton.addEventListener("click", async () => {
        try {
          const options = {
            method: "POST",
            body: JSON.stringify({
              project: newValueLabelInternshipProject.value,
              version: internship.version,
              internshipId: internship.id
            }),
            headers: {
              "Content-Type": "application/json",
              'Authorization': user.token
            },
          };

          const response = await fetch("/api/internships/editProject", options);

          if (!response.ok) {
            throw new Error(
                `fetch error : ${response.status} : ${response.statusText}`
            );
          }

          const editProject = await response.json();
          Navigate("/internship")
          return editProject;

        } catch (error) {
          if (error instanceof Error && error.message.startsWith(
              "fetch error : 400")) {
            errorMessage.innerText = "le sujet du projet est vide !"
          }
          errorMessage.id = "error-message"
          errorMessage.style.display = "block";
        }
        return null;
      });
    });
    valueLabelInternshipProject.className = "valueLabel textCSS d-flex justify-content-between align-items-center";
    valueLabelInternshipProject.appendChild(document.createTextNode(' '));
    valueLabelInternshipProject.appendChild(iconEditProject)
    labelInternshipProject.appendChild(valueLabelInternshipProject);
    divLeft.appendChild(labelInternshipProject);
    divLeft.appendChild(document.createElement("br"))

    // supervisor name
    const labelSupervisorName = document.createElement("label");
    labelSupervisorName.innerText = "Responsable :";
    labelSupervisorName.className = "textCSS";
    const valueLabelSupervisorNameValue = document.createElement("p");
    valueLabelSupervisorNameValue.innerText = `${internship.supervisor.lastname} ${internship.supervisor.firstname}`;
    valueLabelSupervisorNameValue.className = "valueLabel textCSS";
    labelSupervisorName.appendChild(valueLabelSupervisorNameValue);
    divRight.appendChild(labelSupervisorName);
    divRight.appendChild(document.createElement("br"));

    // supervisor phone number
    const labelSupervisorPhoneNumber = document.createElement("label");
    labelSupervisorPhoneNumber.innerText = "Téléphone :";
    labelSupervisorPhoneNumber.className = "textCSS";
    const valueLabelSupervisorPhoneNumber = document.createElement("p");
    valueLabelSupervisorPhoneNumber.innerText = `${internship.supervisor.phoneNumber}`;
    valueLabelSupervisorPhoneNumber.className = "valueLabel textCSS";
    labelSupervisorPhoneNumber.appendChild(valueLabelSupervisorPhoneNumber);
    divRight.appendChild(labelSupervisorPhoneNumber);
    divRight.appendChild(document.createElement("br"));

    // supervisor email
    const labelSupervisorEmail = document.createElement("label");
    labelSupervisorEmail.innerText = "Email :";
    labelSupervisorEmail.className = "textCSS";
    const valueLabelSupervisorEmail = document.createElement("p");
    if (internship.supervisor.email === null) {
      valueLabelSupervisorEmail.innerText = `Aucun email`;
    } else {
      valueLabelSupervisorEmail.innerText = `${internship.supervisor.email}`;
    }
    valueLabelSupervisorEmail.className = "valueLabel textCSS";
    labelSupervisorEmail.appendChild(valueLabelSupervisorEmail);
    divRight.appendChild(labelSupervisorEmail);
    divRight.appendChild(document.createElement("br"));

    divInternshipChild.appendChild(divLeft)
    divInternshipChild.appendChild(divRight)
    divInternshipMother.appendChild(divInternshipChild)

    divMaster.appendChild(divInternshipMother);

    main.appendChild(divMaster);
  }
}

const CreateInternshipPage = async (contact) => {

  const main = document.querySelector('main');
  awaitFront();

  const user = await getAuthenticatedUser();
  setAuthenticatedUser(user);
  Navbar();
  const userToken = getToken();
  if (!userToken) {
    Navigate('/');
    return;
  }

  showNavStyle("dashboard");

  const createInternship = async (supervisor, signatureDate, project) => {

    try {
      const {schoolYear} = contact;
      const options = {
        method: 'POST',
        body: JSON.stringify({
          contact,
          supervisor,
          signatureDate,
          project,
          schoolYear,
        }),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': user.token
        }
      }

      const response = await fetch('/api/internships/create', options);

      if (!response.ok) {
        throw new Error(
            `fetch error : ${response.status} : ${response.statusText}`);
      }

      return await response.json();
    } catch (error) {
      const errorMessage = document.getElementById('error-message-right');
      errorMessage.style.display = "block";
      if (error instanceof Error && error.message.startsWith(
          "fetch error : 400")) {
        errorMessage.innerText = "Mauvaise requête.";
      }
      if (error instanceof Error && error.message.startsWith(
          "fetch error : 409")) {
        errorMessage.innerText = "Numéro de téléphone déjà utilisé";
      }
      if (error instanceof Error && error.message.startsWith(
          "fetch error : 500")) {
        errorMessage.innerText = "Une erreur interne s'est produite, veuillez réssayer";
      }
    }
    return undefined;
  };

  const createSupervisor = async (lastname, firstname, phoneNumber,
      emailToCheck) => {
    try {
      const {company} = contact;
      const email = emailToCheck === "" ? null : emailToCheck;
      const options = {
        method: 'POST',
        body: JSON.stringify({
          company,
          lastname,
          firstname,
          phoneNumber,
          email
        }),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': user.token
        }
      };

      const response = await fetch('/api/supervisors/add', options);

      if (!response.ok) {
        throw new Error(
            `fetch error : ${response.status} : ${response.statusText}`
        );
      }

      return await response.json();
    } catch (error) {
      const errorMessage = document.getElementById('error-message-left');
      errorMessage.style.display = "block";
      if (error instanceof Error && error.message.startsWith(
          "fetch error : 400")) {
        errorMessage.innerText = "Tous les champs doivent être remplis";
      }
      if (error instanceof Error && error.message.startsWith(
          "fetch error : 409")) {
        errorMessage.innerText = "Numéro de téléphone déjà utilisé";
      }
      if (error instanceof Error && error.message.startsWith(
          "fetch error : 500")) {
        errorMessage.innerText = "Une erreur interne s'est produite, veuillez réssayer";
      }
    }
    return undefined;
  }

  const getSupervisors = async () => {
    try {
      const options = {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': user.token
        }
      };

      const response = await fetch(
          `/api/supervisors/allByCompany/${contact.company.id}`, options);

      if (!response.ok) {
        throw new Error(
            `fetch error : ${response.status} : ${response.statusText}`);
      }

      const supervisors = await response.json();
      return supervisors;
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

  const allSupervisors = await getSupervisors();

  let desi = contact.company.designation;
  if (desi === null) {
    desi = "";
  }

  main.innerHTML = `
    <div class="container-create d-flex justify-content-center align-items-end mt-5 mb-5">
      <div class="box-resp d-flex align-items-center flex-column">
        <h1 class="mt-5 mb-5">Responsable</h1>
        <div class="input-group mb-3">
            <span class="input-group-text" id="basic-addon1"><i class="fa-solid fa-user"></i></span>
            <input type="text" class="form-control" id="input-firstname" placeholder="Prénom" aria-label="prénom" aria-describedby="basic-addon1" required>
        </div>
        <div class="input-group mb-3">
            <span class="input-group-text" id="basic-addon1"><i class="fa-solid fa-user"></i></span>
            <input type="text" class="form-control" id="input-lastname" placeholder="Nom" aria-label="nom" aria-describedby="basic-addon1" required>
        </div>
        <div class="input-group mb-3">
            <span class="input-group-text" id="basic-addon1"><i class="fa-solid fa-phone"></i></span>
            <input type="text" class="form-control" id="input-phone" placeholder="Téléphone" aria-label="phone" aria-describedby="basic-addon1" required>
        </div>
        <div class="input-group mb-3">
            <span class="input-group-text" id="basic-addon1"><i class="fa-solid fa-envelope"></i></span>
            <input type="text" class="form-control" id="input-email" placeholder="Email (optionnel)" aria-label="email" aria-describedby="basic-addon1">
        </div>
        <div class="input-group mb-3">
            <span class="input-group-text" id="basic-addon1"><i class="fa-solid fa-building"></i></span>
            <input type="text" class="form-control" id="input-contact" readonly value="${contact.company.name}" aria-label="company" aria-describedby="basic-addon1">
        </div>
        <button type="button" id="resp-register" class="create-button mt-4 ">Ajouter le responsable</button>
        <h2 id="error-message-left"></h2>
      </div>
      <div class="box-company d-flex align-items-center flex-column justify-content-center">
        <h1 class="mb-5">${contact.company.name}<br>${desi}</h1>
        <div class="input-group mb-3">
            <span class="input-group-text" id="basic-addon1"><i class="fa-solid fa-eye"></i></span>
            <input type="text" class="form-control" id="input-supervisor" placeholder="Responsable" aria-label="nom" aria-describedby="basic-addon1" required>
        </div>
        <div class="input-group mb-3">
            <span class="input-group-text" id="basic-addon1"><i class="fa-solid fa-eye"></i></span>
            <input type="date" class="form-control" id="input-date" placeholder="Date" aria-label="date" aria-describedby="basic-addon1" title="Date de signature de la convention" required>
        </div>
        <div class="input-group mb-3">
            <span class="input-group-text" id="basic-addon1"><i class="fa-solid fa-signature"></i></span>
            <input type="text" class="form-control" id="input-subject" placeholder="Sujet (Optionnel)" aria-label="nom" aria-describedby="basic-addon1">
        </div>
        <button type="button" id="stage-register" class="create-button mt-4">Enregistrer</button>
        <h2 id="error-message-right"></h2>
      </div>
    </div>
  `;

  const selectElement = document.getElementById('input-supervisor');

  // eslint-disable-next-line no-unused-vars
  const tomSelectInstance = new TomSelect(selectElement, {
    valueField: 'idSupervisor',
    labelField: 'fullName',
    searchField: ['fullName'],
    maxItems: 1,
    render: {
      no_results() {
        return '<div class="no-results">Aucun superviseur trouvé</div>';
      }
    },
  });

  tomSelectInstance.addOption(allSupervisors.map(supervisor => ({
    idSupervisor: supervisor.id,
    fullName: `${supervisor.firstname} ${supervisor.lastname}`,
  })));

  const registerSupervisorBtn = document.getElementById('resp-register');
  registerSupervisorBtn.addEventListener('click', async () => {
    const lastname = document.getElementById('input-lastname').value;
    const firstname = document.getElementById('input-firstname').value;
    const phoneNumber = document.getElementById('input-phone').value;
    const email = document.getElementById('input-email').value;
    const supervisor = await createSupervisor(lastname, firstname, phoneNumber,
        email);
    if (supervisor) {
      await CreateInternshipPage(contact);
    }
  });

  const registerStageBtn = document.getElementById('stage-register');
  registerStageBtn.addEventListener('click', async () => {
    const supervisorId = document.getElementById('input-supervisor').value;
    let supervisor;
    for (let i = 0; i < allSupervisors.length; i += 1) {
      if (allSupervisors[i].id === parseInt(supervisorId, 10)) {
        supervisor = allSupervisors[i];
        break;
      }
    }
    const signatureDate = document.getElementById('input-date').value;
    let project = document.getElementById('input-subject').value;
    if (project === "") {
      project = null;
    }
    const internship = await createInternship(supervisor, signatureDate,
        project);
    if (internship) {
      Navigate("/internship");
    }
  });

}

export {InternshipPage, CreateInternshipPage};