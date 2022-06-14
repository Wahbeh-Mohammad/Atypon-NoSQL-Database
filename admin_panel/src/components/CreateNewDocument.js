import React, { useState } from "react";
import { Box, Typography, Button, Paper } from "@mui/material";
import "../styles/CreateDocument.css";
import Cookies from "universal-cookie";
import Editor from "@monaco-editor/react";

const CreateNewDocument = (props) => {
    const cookie = new Cookies();
    const token = cookie.get("token");
    const { schemaName, databaseName } = props;
    const [jsonDocument, setJsonDocument] = useState("{\n}");
    const [info, setInfo] = useState("");
    const [infoColor, setInfoColor] = useState("");

    const handleCreate = async () => {
        const creationResponse = await fetch(
            `${process.env.REACT_APP_ADMINCONTROLLER_URL}/write/${databaseName}/${schemaName}/document/new`, {
            method:"POST",
            headers: {
                "content-type": "application/json",
                "authorization":token
            },
            body: jsonDocument
        });

        const jsonResponse = await creationResponse.json();
        const { status, message } = jsonResponse;
        if(status === "GOOD") {
            setInfo(message);
            setInfoColor("green");
        } else {
            setInfo(message);
            setInfoColor("red");
        }
    }

    return (
        <Paper elevation={6} className="padding" sx={{overflow:"hidden"}}>
            <Box className="flex-row-gap create">
                <Box className="flex-col-gap">
                    <Typography variant="h4" color="primary"> Create a document for schema: "{schemaName}"</Typography>
                    <Typography variant="h6"> 
                        Please keep in mind that the document will be validated against the schema. 
                    </Typography>
                    <Box>
                        <Button variant="contained" onClick={handleCreate}> Create new Document </Button>
                    </Box>
                    { info && <Typography color={infoColor} variant="h6"> 
                        {info}
                    </Typography> }
                </Box>
                <Box className="editor">
                    <Editor 
                        height="45rem"
                        width="100%"
                        language={"json"}
                        defaultValue={jsonDocument}
                        onChange={(e) => setJsonDocument(e)} />
                </Box>
            </Box>
        </Paper>
    );
}
 
export default CreateNewDocument;