import React, { useEffect, useState } from 'react';
import { Box, Typography, Paper, Button, TextField } from '@mui/material';
import Node from './Node';

import "../styles/Cluster.css";
import Cookies from 'universal-cookie';

const ClusterControls = (props) => {
    const cookie = new Cookies();
    const [containersInformation, setContainersInformation] = useState([]);
    const [info, setInfo] = useState("");
    const [infoColor, setInfoColor] = useState("");
    const [refreshInfo, setRefreshInfo] = useState("");
    const [refreshInfoColor, setRefreshInfoColor] = useState("");
    const [scaleInfo, setScaleInfo] = useState("");
    const [scaleInfoColor, setScaleInfoColor] = useState("");
    const [numberOfNodes, setNumberOfNodes] = useState(3);

    const handleRefreshAllCaches = () => {
        const readToken = cookie.get("read-token");
        containersInformation.forEach(({ip}) => {
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
                        setRefreshInfo(data.message);
                        setRefreshInfoColor("green");
                        setTimeout(() => {setRefreshInfo("")}, 1500);
                    } else {
                        setRefreshInfo(data.message);
                        setRefreshInfoColor("red");
                    }
                })
            })
    }

    const handleScaleCluster = () => {
        fetch(`${process.env.REACT_APP_CONTAINERDISCOVERY_URL}/cluster/scale/${numberOfNodes}`, {
            method:"POST"
        })
        .then(response => response.json())
        .then(data => {
            if(data.status === "GOOD") {
                setScaleInfo(data.message);
                setScaleInfoColor("green");
            } else {
                setScaleInfo(data.message);
                setScaleInfoColor("red");
            }
        }) 
    }

    useEffect(() => {
        fetch(`${process.env.REACT_APP_CONTAINERDISCOVERY_URL}/cluster/nodes`, {
            method:"GET"
        })
        .then((response) => response.json())
        .then((data) => {
            if(data.status === "GOOD") {
                setContainersInformation(data.content);
            } else {
                setInfo(data.message);
                setInfoColor("red");
            }
        });
    }, []);

    return (
        <Box className="cluster flex-col-gap">
            <Paper elevation={6} className="padding">
                <Typography variant="h4" color="primary"> Nodes </Typography>
            </Paper>
            <Paper elevation={6} className="padding flex-row-gap align-center">
                <Typography variant="h5" color="primary"> Scale the cluster </Typography>
                <TextField variant="outlined" color="primary" label="Number of Nodes" value={numberOfNodes} onChange={e => setNumberOfNodes(e.target.value)} />
                <Button variant="contained" onClick={handleScaleCluster}> Scale </Button>
                { scaleInfo && <Typography color={scaleInfoColor}> {scaleInfo} </Typography> }
            </Paper>
            <Paper elevation={6} className="padding flex-row-gap align-center">
                <Typography variant="h5" color="primary"> Refresh All Caches </Typography>
                <Button variant="contained" onClick={handleRefreshAllCaches}> Refresh </Button>
                { refreshInfo && <Typography color={refreshInfoColor}> {refreshInfo} </Typography> }
            </Paper>
            { containersInformation && 
                <Box className="grid">
                    {
                        containersInformation.map( (containerInfo, idx) => {
                            return ( <Node info={containerInfo} key={idx} /> )
                        }) 
                    }
                </Box>
            }
            { (info) && <Typography variant="h5" color={infoColor}> {info} </Typography>}
        </Box>
    );
}
 
export default ClusterControls;