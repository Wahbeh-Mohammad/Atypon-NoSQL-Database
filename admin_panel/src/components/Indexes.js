import React, { useState } from "react";
import { Box, Button, Paper, TextField, Typography } from "@mui/material";
import Cookies from "universal-cookie";
import Editor from "@monaco-editor/react";
import "../styles/Indexes.css"

const Indexes = (props) => {
    const cookie = new Cookies();
    const [databaseName] = useState(props.databaseName);
    const [schemaName] = useState(props.schemaName);
    const [indexedDocuments, setIndexedDocuments] = useState("[\n]");
    const [fieldName, setFieldName] = useState("");

    const handleIndex = async () => {
        if(!fieldName)
            return;
        const token = cookie.get("read-token");
        if(!token)
            return;
            
        const indexResponse = await fetch(
            `${process.env.REACT_APP_READCONTROLLER_URL}/read/${databaseName}/${schemaName}/indexed?fieldName=${fieldName}`,
            {
                method:"GET",
                headers: {
                    "authorization": token
                }
            });
        const jsonResponse = await indexResponse.json();
        setIndexedDocuments(JSON.stringify(jsonResponse.content, null, 2));
    }


    return (
        <Box className="index-wrapper">
            <Paper className="index-controls flex-row-gap">
                <Typography variant="h4" color="primary">Index</Typography>
                <TextField 
                    autoComplete="off"
                    label="Field name" 
                    size="small" 
                    value={fieldName} 
                    onChange={e=>setFieldName(e.target.value)}/>
                <Button variant="contained" onClick={handleIndex}> Fetch Indexed documents </Button>
            </Paper>
            <Box className="editor">
                <Editor 
                    height="42rem"
                    width="100%"
                    language={"json"}
                    value={indexedDocuments}
                    onChange={(e) => setIndexedDocuments(e)}/>
            </Box>
        </Box>
    );
}
 
export default Indexes;