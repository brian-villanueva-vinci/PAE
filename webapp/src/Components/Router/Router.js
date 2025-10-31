import LoginPage from '../Pages/LoginPage';
import RegisterPage from '../Pages/RegisterPage';
import Logout from '../Logout/Logout';
import ContactPage from '../Pages/ContactPage';
import InfoPage from '../Pages/InfoPage';
import DashboardPage from '../Pages/DashboardPage';
import UserListPage from '../Pages/UserListPage';
import AdminDashboardPage from '../Pages/AdminDashboardPage';
import {CreateInternshipPage, InternshipPage} from "../Pages/InternshipPage";

const routes = {
  "/": LoginPage,
  "/login": LoginPage,
  "/register": RegisterPage,
  "/logout": Logout,
  "/contact": ContactPage,
  "/userList": UserListPage,
  "/info": InfoPage,
  "/dashboard": DashboardPage,
  "/adminBoard": AdminDashboardPage,
  "/internship": InternshipPage,
  "/internship/create": CreateInternshipPage,
};

/**
 * Deal with call and auto-render of Functional Components following click events
 * on Navbar, Load / Refresh operations, Browser history operation (back or next) or redirections.
 * A Functional Component is responsible to auto-render itself : Pages, Header...
 */

const Router = () => {
  /* Manage click on the Navbar */
  const navbarWrapper = document.querySelector("#navbarWrapper");
  navbarWrapper.addEventListener("click", (e) => {
    // To get a data attribute through the dataset object, get the property by the part of the attribute name after data- (note that dashes are converted to camelCase).
    const {uri} = e.target.dataset;

    if (uri) {
      e.preventDefault();
      /* use Web History API to add current page URL to the user's navigation history
       & set right URL in the browser (instead of "#") */
      window.history.pushState({}, uri, window.location.origin + uri);
      /* render the requested component
      NB : for the components that include JS, we want to assure that the JS included
      is not runned when the JS file is charged by the browser
      therefore, those components have to be either a function or a class */
      const componentToRender = routes[uri];
      if (routes[uri]) {
        componentToRender();
      } else {
        throw Error(`The ${uri} ressource does not exist`);
      }
    }
  });

  /* Route the right component when the page is loaded / refreshed */
  window.addEventListener("load", () => {
    const componentToRender = routes[window.location.pathname];
    if (!componentToRender) {
      throw Error(
          `The ${window.location.pathname} ressource does not exist.`
      );
    }

    componentToRender();
  });

  // Route the right component when the user use the browsing history
  window.addEventListener("popstate", () => {
    const componentToRender = routes[window.location.pathname];
    componentToRender();
  });
};

/**
 * Call and auto-render of Functional Components associated to the given URL
 * @param {*} uri - Provides an URL that is associated to a functional component in the
 * routes array of the Router
 */

const Redirect = (uri) => {
  // use Web History API to add current page URL to the user's navigation history & set right URL in the browser (instead of "#")
  window.history.pushState({}, uri, window.location.origin + uri);
  // render the requested component
  const componentToRender = routes[uri];
  if (routes[uri]) {
    componentToRender();
  } else {
    throw Error(`The ${uri} ressource does not exist`);
  }
};

export {Router, Redirect};