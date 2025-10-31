import {
  getAuthenticatedUser,
  getLocalUser,
  getToken,
  setAuthenticatedUser,
  setRemember,
} from "../../utils/session";

import {showNavStyle} from "../../utils/function";
import Navigate from "../../utils/Navigate";
import Navbar from "../Navbar/Navbar";

const onUserLogin = async (userData) => {
  if (document.getElementById("stayconnected").checked) {
    setRemember(true);
    setAuthenticatedUser(userData);
  } else {
    setAuthenticatedUser(userData);
  }
  // re-render the navbar for the authenticated user
  // await Navbar();
  if (userData.user.role === 'Professeur' || userData.user.role
      === 'Administratif') {
    Navigate('/adminBoard');
  } else if (userData.user.role === 'Etudiant') {
    Navigate('/dashboard');
  } else {
    Navigate('/');
  }
};

async function login(e) {
  e.preventDefault();
  let errorMessage = null;

  const email = document.querySelector("#input-email").value;
  const password = document.querySelector("#input-pwd").value;

  const userTemp = await getAuthenticatedUser();
  setAuthenticatedUser(userTemp);
  Navbar();

  if (userTemp) {
    // re-render the navbar for the authenticated user
    // Navbar();
    Navigate("/");
  } else {
    try {
      const options = {
        method: "POST", // *GET, POST, PUT, DELETE, etc.
        body: JSON.stringify({
          email,
          password,
        }), // body data type must match "Content-Type" header
        headers: {
          "Content-Type": "application/json",
        },
      };

      const response = await fetch("/api/users/login", options); // fetch return a promise => we wait for the response

      if (!response.ok) {
        throw new Error(
            `fetch error : ${response.status} : ${response.statusText}`
        );
      }

      const user = await response.json(); // json() returns a promise => we wait for the data

      // eslint-disable-next-line no-use-before-define
      await onUserLogin(user);

    } catch (error) {
      errorMessage = document.getElementById("error-message");
      errorMessage.style.display = "block";
    }
  }
}

const keypressSubmit = async (e) => {
  if (e.key === 'Enter') {
    await login(e);
  }
}

const LoginPage = async () => {
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
          <div class="box-register d-flex justify-content-center align-items-center">
            <p class="btn-register">Inscription</p>
          </div>
          <div class="box-login d-flex justify-content-center align-items-center login-slide-in">
            <div class="box-in-login d-flex justify-content-center align-items-center flex-column">
              <h1 class="opacity-animation">Connexion</h1>
              <div class="input-group mb-3 opacity-animation">
                <span class="input-group-text" id="basic-addon1"><i class="fa-solid fa-envelope"></i></span>
                <input type="text" class="form-control" id="input-email" placeholder="Adresse email" aria-label="Adresse email" aria-describedby="basic-addon1">
              </div>
              <div class="input-group mb-3 opacity-animation">
                <span class="input-group-text" id="basic-addon1"><i class="fa-solid fa-key"></i></span>
                <input type="password" class="form-control" id="input-pwd" placeholder="Mot de passe" aria-label="Mot de passe" aria-describedby="basic-addon1">
              </div>
              <div class="form-check mb-3 opacity-animation">
                <input class="form-check-input" type="checkbox" value="" id="showPwd">
                <label class="form-check-label" for="showPwd">
                  Afficher le mot de passe
                </label>
              </div>
              <div class="form-check mb-3 opacity-animation">
                <input class="form-check-input" type="checkbox" value="" id="stayconnected">
                <label class="form-check-label" for="stayconnected">
                  Se souvenir de moi
                </label>
              </div>
              <h2 id="error-message">L'adresse email ou<br>le mot de passe est incorrect !</h2>
              <p class="btn-login opacity-animation" id="login-btn">Se connecter</p>
            </div>
          </div>
        </div>
    `;
  const registerBtn = document.querySelector(".btn-register");
  registerBtn.addEventListener('click', () => {
    Navigate("/register");
  });

  document.getElementById('showPwd').addEventListener('change', function () {
    const inputPwd = document.getElementById('input-pwd');
    inputPwd.type = this.checked ? 'text' : 'password';
  });

  showNavStyle("login");

  const loginBtn = document.getElementById("login-btn");

  loginBtn.addEventListener("click", login);

  const inputEmail = document.getElementById("input-email");
  const inputPwd = document.getElementById("input-pwd");

  inputEmail.addEventListener("keypress", keypressSubmit);
  inputPwd.addEventListener("keypress", keypressSubmit);
};

export default LoginPage;