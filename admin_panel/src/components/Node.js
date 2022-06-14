import React, { useState } from 'react';
import { Typography,Box, Paper, Button } from '@mui/material';
import Cookies from 'universal-cookie';

const Node = (props) => {
    const { hostname, state, ip, containerName } = props.info;
    const cookie = new Cookies();
    const [readToken] = useState(cookie.get("read-token"));
    const [info, setInfo] = useState("");
    const [infoColor, setInfoColor] = useState("");

    const handleRefreshCache = () => {
        fetch(`http://${ip}:8001/read/cache/refresh`, {
            method:"GET",
            headers: {
                "authorization": readToken
            }
        })
        .then(response => response.json())
        .then(data => {
            console.log(data);
            if(data.status === "GOOD") {
                setInfo(data.message);
                setInfoColor("green");
                setTimeout(() => {setInfo("");setInfoColor("");}, 1500);
            } else {
                setInfo(data.message);
                setInfoColor("red");    
            }
        })
    }

    return (
        <Paper elevation={6} className="node flex-col-gap">
            <Typography variant="h6" color="primary"> Node Hostname : {hostname} </Typography>
            <Typography variant="h6" color="primary"> Node Name : {containerName.substr(1)} </Typography>
            <Typography variant="h6" color="primary"> Node State : {state} </Typography>
            <Typography variant="h6" color="primary"> Node IP : {ip} </Typography>
            <Box className="flex-row-gap">
                <Button size="large" variant="contained" onClick={handleRefreshCache}> Refresh Cache </Button>
                { info && <Typography color={infoColor} variant="h6" className="flex-row-gap align-center"> { info } </Typography> }
            </Box>
        </Paper>
    );
}
 
export default Node;