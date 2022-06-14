import React, { useState, useEffect } from "react";
import Cookies from "universal-cookie";
import { Box, Paper, Typography, TextField, Button } from "@mui/material";

import "../styles/Login.css";

const Login = (props) => {
    const cookie = new Cookies();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [info, setInfo] = useState("");

    const handleLogin = async () => {
        setInfo("");
        if(username === "" || username === null) {
            setInfo("Invalid username");
            return;
        }

        if(password === "" || password === null) {
            setInfo("Invalid password");
            return;
        }

        const body = { username, password };
        const response = await fetch(`${process.env.REACT_APP_ADMINCONTROLLER_URL}/auth/login`, {
            method:"POST",
            headers: {
                "content-type":"application/json"
            },
            body : JSON.stringify(body)
        });

        const data = await response.json();

        if(data.status === "GOOD") {
            // verify admin login 
            const token = data.content;
            fetch(`${process.env.REACT_APP_ADMINCONTROLLER_URL}/auth/user/verifyAdmin`, {
                method:"GET",
                headers: {
                    "authorization": token
                }
            })
            .then(response => response.json())
            .then(data => {
                if(data.status === "GOOD") {
                    cookie.set("token", token);
                    if(username === "admin" && password === "admin") {
                        window.location.assign("/creds");
                    } else {
                        // get a token from read controllers
                        fetch(`${process.env.REACT_APP_READCONTROLLER_URL}/auth/login`, {
                            method:"POST",
                            headers: {
                                "content-type": "application/json"
                            },
                            body : JSON.stringify(body)
                        })
                        .then(response => response.json())
                        .then(data => {
                            console.log(data);
                            cookie.set("read-token", data.content);
                            window.location.assign("/dashboard");
                        });
                    }
                } else {
                    setInfo(data.message);
                }
            });
        } else {
            setInfo(data.message);
        }
    }

    useEffect(()=>{
        const token = cookie.get("token");
        if(token) {
            fetch(`${process.env.REACT_APP_ADMINCONTROLLER_URL}/auth/user/verifyAdmin`, {
                method:"GET",
                headers: {
                    "authorization": token
                }
            })
            .then(response => response.json())
            .then(data => {
                if(data.status === "GOOD") {
                    window.location.assign("/dashboard");
                } else {
                    cookie.remove("token");
                    cookie.remove("read-token");
                }
            });
        } 
    },[]);

    return ( 
        <Box className="login-wrapper">
            <Paper className="flex-col-gap login" elevation={5}>
                <Typography variant="h4"> Login </Typography>
                <TextField 
                    autoComplete="off"
                    variant="outlined" 
                    type="text" 
                    label="Username" 
                    value={username || ''} 
                    onChange={e => setUsername(e.target.value)} />
                <TextField 
                    autoComplete="off"
                    variant="outlined" 
                    type="password" 
                    label="Password" 
                    value={password || ''} 
                    onChange={e => setPassword(e.target.value)} />
                <Button size="large" color="primary" variant="contained" onClick={handleLogin}> Login </Button>
                {info && <Typography align="center" variant="h6" color="red"> {info} </Typography>}
            </Paper>
        </Box>
    );
}
 
export default Login;