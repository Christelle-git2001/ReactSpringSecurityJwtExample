import {Outlet} from "react-router-dom";
import Header from "./Header.jsx";
import Footer from "./Footer.jsx";
import '../../css/PageLayout.css'
function PageLayout ({user}) {
  return (
    <div id="pagelayout" className="pageLayout">
      <Header user={user}/>
      <Outlet />
      <Footer />
    </div>
  );
}
export default PageLayout;
