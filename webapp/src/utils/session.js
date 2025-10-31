const STORE_NAME = "user";
let currentUser;
let remember = false;
let localUser;

const setLocalUser = (authenticatedUser) => {
  localUser = authenticatedUser;
}

const getLocalUser = () => localUser;

const getRemember = () => remember;

const setRemember = (change) => {
  remember = change;
}

const setUserSessionStorage = (authenticatedUser) => {
  const serializedToken = authenticatedUser.token;
  sessionStorage.setItem(STORE_NAME, serializedToken);
  currentUser = JSON.stringify(authenticatedUser);
};

const setUserLocalStorage = (authenticatedUser) => {
  const serializedToken = authenticatedUser.token;
  localStorage.setItem(STORE_NAME, serializedToken);
  currentUser = JSON.stringify(authenticatedUser);
};

const removeSessionData = () => {
  localStorage.removeItem(STORE_NAME);
  sessionStorage.removeItem(STORE_NAME);
  currentUser = null;
  remember = false;
  setLocalUser(null);
};

const getToken = () => {
  let token;
  if (remember === true) {
    token = localStorage.getItem(STORE_NAME)
  } else {
    token = sessionStorage.getItem(STORE_NAME);
  }

  if (!token) {
    return undefined;
  }

  return token;
}

const setAuthenticatedUser = (authenticatedUser) => {
  if (!authenticatedUser) {
    removeSessionData();
  } else if (getRemember()) {
    setUserLocalStorage(authenticatedUser);
    setLocalUser(authenticatedUser.user);
  } else {
    setUserSessionStorage(authenticatedUser);
    setLocalUser(authenticatedUser.user);
  }
}

const getAuthenticatedUser = async () => {
  let token;
  if (remember === true) {
    token = localStorage.getItem(STORE_NAME)
  } else {
    token = sessionStorage.getItem(STORE_NAME);
  }

  if (!token) {
    return undefined;
  }

  const options = {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token
    }
  };

  const response = await fetch("/api/users/checkToken", options);
  if (!response.ok) {
    currentUser = undefined;
  } else {
    currentUser = await response.json();
  }

  return currentUser;
}

export {
  setLocalUser,
  getLocalUser,
  setRemember,
  getRemember,
  getToken,
  setAuthenticatedUser,
  getAuthenticatedUser,
  setUserSessionStorage,
  setUserLocalStorage,
  removeSessionData,
};
