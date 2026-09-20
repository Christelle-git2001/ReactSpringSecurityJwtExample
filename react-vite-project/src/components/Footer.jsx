import React from "react";
import { Link } from 'react-router-dom';
import './Footer.css';
function Footer() {
  return (
    <footer className="footercss">
      <p>Copyright &copy; 2026</p>
      <Link to='/about'>About</Link>
    </footer>
  );
}
export default Footer;
