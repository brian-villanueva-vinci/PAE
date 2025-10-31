import Navbar from "../Navbar/Navbar";
import {removeSessionData} from "../../utils/session";
import Navigate from "../../utils/Navigate";

const Logout = () => {
  removeSessionData();
  // re-render the navbar for a non-authenticated user
  Navbar();
  Navigate("/");
};

export default Logout;