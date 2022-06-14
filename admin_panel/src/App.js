import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
// views
import { Login, ChangeCredentials, Dashboard } from "./views";

import "./styles/global.css";

function App() {
  return (
    <Router>
      <Routes>
        <Route exact path="/" element={ <Login/> } />
        <Route exact path="/creds" element={ <ChangeCredentials /> } />
        <Route exact path="/dashboard" element={ <Dashboard /> } />
      </Routes>
    </Router>
  );
}

export default App;
