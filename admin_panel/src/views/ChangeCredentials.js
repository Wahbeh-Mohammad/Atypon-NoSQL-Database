import React, { useState, useEffect } from 'react';
import Cookies from 'universal-cookie';
import { Box, Typography, Button, TextField, Paper } from "@mui/material";

import "../styles/ChangeCredentials.css";

const ChangeCredentials = (props) => {
    const cookie = new Cookies();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [info, setInfo] = useState("");

    const handleSubmit = async () => {
        setInfo("");
        if(username === "" || username === null) {
            setInfo("Invalid username");
            return;
        }
        if(password === "" || password === null) {
            setInfo("Invalid password");
            return;
        }

        const token = cookie.get("token");
        const body = { username, password };

        const response = await fetch(`${process.env.REACT_APP_ADMINCONTROLLER_URL}/admin/user/update`, {
            method: "PUT",
            headers: {
                "content-type":"application/json",
                "authorization": token
            },
            body: JSON.stringify(body)
        });

        const data = await response.json();
        if(data.status === "GOOD") {
            cookie.remove("token");
            window.location.assign("/");
        } else {
            setInfo(data.message);
        }
    }

    useEffect(()=>{
        const token = cookie.get("token");
        if(token == null) {
            window.location.assign("/");
        } 
    },[])

    return (
        <Box className="change-wrapper">
            <Paper elevation={6} className="change shadow">
                <Typography color="primary" variant="h4"> Admin Credentials </Typography>
                <TextField label="Username" type="text" value={username} onChange={(e) => setUsername(e.target.value)} variant="outlined" />
                <TextField label="Password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} variant="outlined" />
                <Button variant="contained" onClick={handleSubmit}> Change credentials </Button>
                { info && <Typography color="red">{info}</Typography> }
            </Paper>
        </Box>
    );
}

export default ChangeCredentials;