import React, { useState } from "react";
import { Paper, Box, Button, Typography } from "@mui/material";

import "../styles/Document.css";
import Editor from "@monaco-editor/react";
import Cookies from "universal-cookie";
import InfoIcon from '@mui/icons-material/Info';

const Document = (props) => {
    const cookie = new Cookies();
    const [token] = useState(cookie.get("token"));
    const [databaseName] = useState(props.databaseName);
    const [schemaName] = useState(props.schemaName);
    const [fullDocument, setFullDocument] = useState(JSON.stringify(props.fullDocument, null , 2));
    const [id] = useState(props.fullDocument._id);
    const [infoColor, setInfoColor] = useState("");
    const [info, setInfo] = useState("");

    const handleUpdate = async () => {
        setInfo("");
        setInfoColor("");
        const updateResponse = await fetch(`${process.env.REACT_APP_ADMINCONTROLLER_URL}/update/${databaseName}/${schemaName}/document?documentId=${id}`,{
            method: "PUT",
            headers: {
                "content-type": "application/json",
                "authorization": token
            },
            body: fullDocument
        });
        const updateJSON = await updateResponse.json();
        const { message, status } = updateJSON;
        if(status === "GOOD") {
            setInfo(message);
            setInfoColor("green");
        } else {
            setInfo(message);
            setInfoColor("red");
        }
    }

    const handleDelete = async () => {
        setInfo("");
        setInfoColor("");
        const updateResponse = await fetch(`${process.env.REACT_APP_ADMINCONTROLLER_URL}/delete/${databaseName}/${schemaName}/document?documentId=${id}`,{
            method: "DELETE",
            headers: {
                "content-type": "application/json",
                "authorization": token
            }
        });
        const updateJSON = await updateResponse.json();
        const { message, status } = updateJSON;
        if(status === "GOOD") {
            setInfo(message);
            setInfoColor("green");
        } else {
            setInfo(message);
            setInfoColor("red");
        }
        setFullDocument(null);
    }
    
    return ( <>
        {!fullDocument && <></>}
        {fullDocument && 
            <Paper className="full-document padding">
                <Box className="data">
                    <Editor 
                        options={{
                            minimap: {
                                enabled: false
                            },
                            scrollbar: {
                                enabled: false
                            },
                            scrollBeyondLastLine: {
                                enabled: false
                            }
                        }}
                        height="15rem"
                        width="100%"
                        language={"json"}
                        defaultValue={fullDocument}
                        onChange={(e) => setFullDocument(e)} />
                </Box>
                <Box className="document-operations">
                    <Box className="flex-row-gap">
                        <Button variant="contained" color="warning" onClick={handleUpdate}> Update document </Button>
                        <Button variant="contained" color="error" onClick={handleDelete}> Delete document </Button>
                    </Box>
                    { info && <Typography color={infoColor} className="flex-row-gap align-center"> <InfoIcon /> {info} </Typography>}
                </Box>
            </Paper>
        } </>
    );
}
 
export default Document;