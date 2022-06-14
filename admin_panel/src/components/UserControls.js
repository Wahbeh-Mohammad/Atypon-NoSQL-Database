import React, { useState, useEffect } from 'react';
import Cookies from 'universal-cookie';

import { Box, List, Divider, Typography, ListItemButton, TextField,
         InputLabel, Select, MenuItem, FormControl, Button } from '@mui/material';
import InfoIcon from '@mui/icons-material/Info';

import UserDocument from './UserDocument';
import "../styles/AllUsers.css";

const UserControls = (props) => {
    const cookie = new Cookies();
    const [token] = useState(cookie.get("token"));
    const [view, setView] = useState('all-users');

    const [allUsers, setAllUsers] = useState([]);

    const fetchAllUsers = () => {
        fetch(`${process.env.REACT_APP_ADMINCONTROLLER_URL}/admin/user/all`, {
            method:"GET",
            headers: {
                "authorization": token
            }
        })
        .then(response => response.json())
        .then(data => {
            if(data.status === "GOOD") {
                setAllUsers(data.content);
            }   
        });
    }

    const [newUsername, setNewUsername] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [newRole, setNewRole] = useState("REGULAR");
    const [info, setInfo] = useState("");
    const [infoColor, setInfoColor] = useState("");

    const handleChange = (e) => {
        setNewRole(e.target.value);
    }

    const handleSubmitNewUser = () => {
        if(newUsername === null || newUsername === ""){
            setInfo("Username cannot be empty/null");
            setInfoColor("red");
            return;
        }
        if(newPassword === null || newPassword === ""){
            setInfo("Password cannot be empty/null");
            setInfoColor("red");
            return;
        }

        fetch(`${process.env.REACT_APP_ADMINCONTROLLER_URL}/admin/user/new`, {
            method:"POST",
            headers: {
                "content-type":"application/json",
                "authorization": token
            },
            body: JSON.stringify({username: newUsername, password: newPassword, role: newRole})
        })
        .then(response => response.json())
        .then(data => {
            if(data.status === "GOOD") {
                setInfo(data.message);
                setInfoColor("green");
            } else {
                setInfo(data.message);
                setInfoColor("red");
            }
        });
    }

    useEffect(()=>{
        if(view === "all-users") {
            fetchAllUsers();
        }
    }, [view]);

    useEffect(()=>{
        fetchAllUsers();
    }, []);

    return (
        <Box className="users-controls">
            <List className="view-controls">
                <ListItemButton onClick={e => setView("all-users")}>
                    <Typography color="primary" variant="h6"> All Users </Typography>
                </ListItemButton>
                <Divider />
                <ListItemButton onClick={e => setView("create-user")}>
                    <Typography color="primary" variant="h6"> Create new User </Typography>
                </ListItemButton>
            </List>
            <Box className="body overflow-scroll">
                <Box className="flex-col-gap">
                    { view === "all-users" && 
                        Object.keys(allUsers).map((key) => {
                            return (
                                <UserDocument key={key} userData={allUsers[key]}  />
                            )
                        })
                    }
                    { view === "create-user" && 
                        <Box className="create-user flex-col-gap">
                            <Typography variant="h4" color="primary"> Create a new User </Typography>
                            <Typography variant="h6"> Roles: </Typography>
                            <Typography variant="h6"> - Admin: is allowed to perform CRUD operations on the databases. </Typography>
                            <Typography variant="h6"> - Regular: is only allowed to read from the databases. </Typography>
                            <Divider />
                            <Box className="flex-col-gap main-form">
                                <TextField value={newUsername} autoComplete="off" onChange={e => setNewUsername(e.target.value)} label="Username" />
                                <TextField value={newPassword} autoComplete="off" onChange={e => setNewPassword(e.target.value)} label="Password" />
                                <FormControl>
                                    <InputLabel id="role-label">Role</InputLabel>
                                    <Select
                                        labelId="role-label"
                                        id="role-label"
                                        value={newRole}
                                        label="Role"
                                        onChange={handleChange}>
                                        <MenuItem value={"ADMIN"}>Admin</MenuItem>
                                        <MenuItem value={"REGULAR"}>Regular</MenuItem>
                                    </Select>
                                </FormControl>
                                <Box className="flex-col-gap">
                                    <Button variant="contained" onClick={handleSubmitNewUser}> Create new User </Button>
                                    { info && 
                                        <Typography variant="h5" className="flex-row-gap align-center" color={infoColor}> 
                                            <InfoIcon /> {info} 
                                        </Typography>
                                    }
                                </Box>
                            </Box>
                        </Box>
                    }
                </Box>
            </Box>
        </Box>
    );
}
 
export default UserControls;