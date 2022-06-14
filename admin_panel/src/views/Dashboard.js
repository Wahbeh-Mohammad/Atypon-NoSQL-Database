import React, { useState, useEffect } from "react";
import Cookies from "universal-cookie";
// components
import { Box, Typography, Button, Divider } from "@mui/material";
import StorageIcon from '@mui/icons-material/Storage';
import { DatabaseInformation, CreateDatabase, UserControls } from "../components";

// styles
import "../styles/Dashboard.css"
import ClusterControls from "../components/ClusterControls";

const Dashboard = (props) => {
    const cookie = new Cookies();

    // database related
    const [allDatabases, setAllDatabases] = useState([]);
    const [databaseConnectedTo, setDatabaseConnectedTo] = useState("");
    const [connectedToDatabase, setConnectedToDatabase] = useState(false);

    const handleDatabaseChange = (dbName) => {
        setDatabaseConnectedTo(dbName);
        setConnectedToDatabase(true);
    }

    // view controls
    const [mainView, setMainView] = useState("database");

    const handleMainViewChange = (view) => {
        setMainView(view);
        setConnectedToDatabase(false);
        setDatabaseConnectedTo("");
    }

    const handleLogout = () => {
        cookie.remove("token");
        cookie.remove("read-token");
        window.location.assign("/");
    }

    // Effect handlers
    useEffect(() => {
        // fetch database names from server
        const token = cookie.get("token");
        if(!token)
            return;
        fetch(`${process.env.REACT_APP_ADMINCONTROLLER_URL}/admin/database/all`, {
            method:"GET",
            headers: {
                "authorization": token
            }
        })
        .then((response) => response.json())
        .then((data) => {
            if(data.status === "GOOD") {
                setAllDatabases(data.content);
            }
        })
    }, []);

    useEffect(() => {
        // check validity of token
        const token = cookie.get("token");
        if(!token) {
            window.location.assign("/");
        } else {
            fetch(`${process.env.REACT_APP_ADMINCONTROLLER_URL}/auth/user/verifyAdmin`, {
                method:"GET",
                headers: {
                    "authorization": token
                }
            })
            .then((response) => response.json())
            .then((data) => {
                if(data.status !== "GOOD") {
                    cookie.remove("token");
                    window.location.assign("/");
                }
            });
        }
    }, []);

    return (
        <Box className="dashboard">
            <Box className="header">
                <Box className="sub-header-1">
                    <Typography variant="h6"> 
                        <Typography variant="h6" color="primary" component="span">ATYPON</Typography> NoSQL Dashboard 
                    </Typography>
                    <Box className="links">
                        <Button onClick={() => handleMainViewChange("database")}> Database Controls </Button>
                        <Button onClick={() => handleMainViewChange("users")}> Users </Button>
                        <Button onClick={() => handleMainViewChange("cluster")}> Cluster Controls </Button>
                    </Box>
                    <Box className="logout">
                        <Button variant="contained" size="small" onClick={handleLogout}> Logout </Button>
                    </Box>
                </Box>
                <Divider/>
                { mainView === "database" &&
                    <>
                        <Box className="sub-header-2 align-center">
                            <Typography className="flex-row align-center" variant="h6">
                                <StorageIcon fontSize="medium" color="primary" />
                            </Typography>
                            { 
                                allDatabases.map((databaseName, idx) => {
                                    return ( <Button value={databaseName} size="medium" key={idx}
                                                    onClick={e => handleDatabaseChange(e.target.value)}> {databaseName} </Button> )
                                }) 
                            }
                        </Box>
                        <Divider/>
                    </>
                }
            </Box>
            <Box className="main-body">
                { mainView === "database" && 
                    <Box className="create-database-wrapper"> 
                        { !connectedToDatabase && <CreateDatabase />}
                        { connectedToDatabase && <DatabaseInformation databaseName={databaseConnectedTo} /> }
                    </Box> 
                }
                { mainView === "cluster" && <ClusterControls /> }
                { mainView === "users" && <UserControls /> }
            </Box>
        </Box>
    );
}
 
export default Dashboard;