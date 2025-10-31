import {showNavStyle} from "../../utils/function";
import Navbar from "../Navbar/Navbar";
import Navigate from "../../utils/Navigate";
import {
  getAuthenticatedUser,
  getLocalUser,
  getToken,
  setAuthenticatedUser
} from "../../utils/session";

const RegisterPage = async () => {
  const loggedUser = await getAuthenticatedUser();
  setAuthenticatedUser(loggedUser);
  Navbar();
  const userToken = getToken();
  const localUser = getLocalUser();
  if (userToken) {
    if (localUser.role === "Etudiant") {
      Navigate('/dashboard');
      return;
    }
    if (localUser.role === 'Professeur' || localUser.role === 'Administratif') {
      Navigate('/adminBoard');
      return;
    }
  }

  const main = document.querySelector('main');
  main.innerHTML = ` 
        <div class="page-login d-flex justify-content-center align-items-center mb-4 mt-5">
          <div class="box-login d-flex justify-content-center align-items-center register-slide-in" id="box-register-left">
            <div class="box-in-login d-flex justify-content-center align-items-center flex-column">
              <h1 class="opacity-animation">Inscription</h1>
              <div class="input-group mb-3 opacity-animation">
                <span class="input-group-text" id="basic-addon1"><i class="fa-solid fa-user"></i></span>
                <input type="text" class="form-control" id="input-firstname" placeholder="Prénom" aria-label="Prénom" aria-describedby="basic-addon1">
              </div>
              <div class="input-group mb-3 opacity-animation">
                <span class="input-group-text" id="basic-addon1"><i class="fa-solid fa-user"></i></span>
                <input type="text" class="form-control" id="input-lastname" placeholder="Nom" aria-label="Nom" aria-describedby="basic-addon1">
              </div>
              <div class="input-group mb-3 opacity-animation">
                <span class="input-group-text" id="basic-addon1"><i class="fa-solid fa-envelope"></i></span>
                <input type="text" class="form-control" id="input-email" placeholder="Adresse email" aria-label="Adresse email" aria-describedby="basic-addon1">
              </div>
              <div class="input-group mb-3 opacity-animation">
                <span class="input-group-text" id="basic-addon1"><i class="fa-solid fa-phone"></i></span>
                <input type="tel" class="form-control" id="input-phone-number" placeholder="Téléphone" aria-label="Téléphone" aria-describedby="basic-addon1">
              </div>
              <div class="input-group mb-3 opacity-animation">
                <span class="input-group-text" id="basic-addon1"><i class="fa-solid fa-key"></i></span>
                <input type="password" class="form-control" id="input-pwd" placeholder="Mot de passe" aria-label="Mot de passe" aria-describedby="basic-addon1">
              </div>
              <div class="input-group-role disable">
                <div class="form-check form-check-inline">
                  <input class="form-check-input" type="radio" name="inlineRadioOptions" id="roleTeacher" value="Professeur">
                  <label class="form-check-label" for="roleTeacher">Professeur</label>
                </div>
                <div class="form-check form-check-inline">
                  <input class="form-check-input" type="radio" name="inlineRadioOptions" id="roleAdministrative" value="Administratif">
                  <label class="form-check-label" for="roleAdministrative">Administratif</label>
                </div>
              </div>
              <p class="btn-login opacity-animation" id="register-btn">S'inscrire</p>
              <h2 id="error-message">L'adresse email ou<br>le mot de passe est incorrect !</h2>
            </div>
          </div>
          <div class="box-register d-flex justify-content-center align-items-center" id="box-register-right">
            <p class="btn-register">Connexion</p>
          </div>
        </div>
  `;

  showNavStyle("register");

  const loginBtn = document.querySelector(".btn-register");
  loginBtn.addEventListener('click', () => {
    Navigate("/login");
  });

  const registerBtn = document.getElementById("register-btn");
  registerBtn.addEventListener("click", register);

  const emailInput = document.getElementById("input-email");
  emailInput.addEventListener("input", roleSelector);

  const inputs = document.querySelectorAll('.form-control');
  inputs.forEach(input => {
    input.addEventListener('keypress', async (event) => {
      if (event.key === "Enter") {
        await register(event);
      }
    })
  })

};

async function register(e) {
  e.preventDefault();
  let errorMessage = null;

  const firstname = document.querySelector("#input-firstname").value;
  const lastname = document.querySelector("#input-lastname").value;
  const email = document.querySelector("#input-email").value;
  const phoneNumber = document.querySelector("#input-phone-number").value;
  const password = document.querySelector("#input-pwd").value;
  const roleRadio = document.querySelector(".input-group-role");
  let role;
  const roleRadioBtn = document.querySelectorAll(
      '.input-group-role input[type="radio"]');

  errorMessage = document.getElementById("error-message");
  try {
    if (roleRadio && !roleRadio.classList.contains("disable")) {
      roleRadioBtn.forEach(button => {
        if (button.checked) {
          role = button.value;
        }
      })
      if (!role) {
        throw new Error(
            `fetch error : 400 : BADREQUEST`
        );
      } else {
        errorMessage.style.display = "none";
      }
    }

    if (!role) {
      role = "Etudiant";
    }
  } catch (error) {
    errorMessage.style.display = "block";
    errorMessage.innerText = "role non choisi";
    return;
  }

  try {
    const options = {
      method: "POST", // *GET, POST, PUT, DELETE, etc.
      body: JSON.stringify({
        email,
        lastname,
        firstname,
        phoneNumber,
        password,
        role,
      }), // body data type must match "Content-Type" header
      headers: {
        "Content-Type": "application/json",
      },
    };

    const response = await fetch("/api/users/register", options); // fetch return a promise => we wait for the response

    if (!response.ok) {
      throw new Error(
          `fetch error : ${response.status} : ${response.statusText}`
      );
    }
  } catch (error) {
    errorMessage = document.getElementById("error-message");
    errorMessage.style.display = "block";
    if (error instanceof Error && error.message.startsWith(
        "fetch error : 400")) {
      errorMessage.innerText = "Tous les champs doivent être remplis";
    }
    if (error instanceof Error && error.message.startsWith(
        "fetch error : 409")) {
      errorMessage.innerText = "Email déjà utilisé";
    }
    if (error instanceof Error && error.message.startsWith(
        "fetch error : 500")) {
      errorMessage.innerText = "Une erreur interne s'est produite, veuillez réssayer";
    }
    return;
  }
  Navigate("/");
}

function roleSelector() {
  const emailInput = document.getElementById("input-email").value;
  const mailStudent = /@student\.vinci\.be$/;
  const mailNonStudent = /@vinci\.be$/;
  const roleInput = document.querySelector(".input-group-role")
  if (mailNonStudent.test(emailInput)) {
    roleInput.classList.remove("disable");
  } else if (mailStudent.test(emailInput)) {
    roleInput.classList.add("disable")
  }
}

export default RegisterPage;