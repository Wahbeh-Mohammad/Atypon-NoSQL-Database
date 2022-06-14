import React, { useState } from "react";
import Cookies from "universal-cookie";
import { Typography, Box, Divider, TableBody, Table, TableRow, 
        TableHead, TableCell, TableContainer, Paper, Button } from "@mui/material";
import Editor from "@monaco-editor/react";
import { validateSchema } from "../utils/validation";
import SchemaIcon from '@mui/icons-material/Schema';
import InfoIcon from '@mui/icons-material/Info';
import "../styles/CreateSchema.css";

const sampleSchema = `{
    "schemaName": "exampleSchema",
    "schema": {
        "fieldName":"fieldType"
    }
}`

const CreateSchema = (props) => {
    const cookie = new Cookies();
    const { databaseName } = props;
    const [token] = useState(cookie.get("token"));
    const [jsonSchema, setJsonSchema] = useState(sampleSchema);
    const [info, setInfo] = useState("");
    const [infoColor,setInfoColor] = useState("");

    const handleSubmit = async ()=>{
        setInfo("");
        setInfoColor("");
        const validation = validateSchema(jsonSchema);
        if(validation.valid) {
            const creationResponse = await fetch(`${process.env.REACT_APP_ADMINCONTROLLER_URL}/write/${databaseName}/schema/new`, {
                method:"POST",
                headers: {
                    "content-type":"application/json",
                    "authorization":token
                },
                body: jsonSchema
            });
            const jsonResponse = await creationResponse.json();
            const { status, message } = jsonResponse;
            console.log(jsonResponse);
            if(status === "GOOD") {
                window.location.reload();
            } else {
                setInfoColor("red");
                setInfo(message);
            }
        } else {
            setInfo(validation.info);
            setInfoColor(validation.color);
        }
    }
    
    return (
        <Box className="create-schema-wrapper">
            <Paper elevation={6} className="create-schema">
                <Box className="creation-tut">
                    <Typography color="primary" variant="h4"> <SchemaIcon /> Create a new Schema </Typography>
                    <Divider />
                    <Box className="schema-tutorial">
                        <Typography variant="h5"> This is a simple guide to create a new schema successfully.</Typography>
                        <Typography variant="h6">
                            There is 2 things that you need to deliver:
                        </Typography>
                        <Typography variant="h6">
                            1. property "schemaName", the schema's name.
                        </Typography>
                        <Typography variant="h6">
                            2. property "schema", the schema itself.
                        </Typography>
                        <Divider />
                        <Typography variant="h6">
                            The schema property should have all fields, 
                            do not add an id property, this will be handled by the database server.
                        </Typography>
                    </Box>
                    <TableContainer component={Paper}>
                        <Table>
                            <TableHead>
                                <TableRow >
                                    <TableCell align="center"> Data Type </TableCell>
                                    <TableCell align="center"> Value in Schema </TableCell>
                                    <TableCell align="center"> Example Data </TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                <TableRow >
                                    <TableCell align="center"> Yes/No </TableCell>
                                    <TableCell align="center"> Boolean </TableCell>
                                    <TableCell align="center"> true, false </TableCell>
                                </TableRow>
                                <TableRow >
                                    <TableCell align="center"> Numbers </TableCell>
                                    <TableCell align="center"> Integer </TableCell>
                                    <TableCell align="center"> 1,2,3... </TableCell>
                                </TableRow>
                                <TableRow >
                                    <TableCell align="center"> Text </TableCell>
                                    <TableCell align="center"> String </TableCell>
                                    <TableCell align="center"> "Hello, World!" </TableCell>
                                </TableRow>
                                <TableRow >
                                    <TableCell align="center"> lists </TableCell> 
                                    <TableCell align="center"> Array </TableCell>
                                    <TableCell align="center"> [1,2, "Hello", true] </TableCell>
                                </TableRow>
                            </TableBody>
                        </Table>
                    </TableContainer>
                    <Divider />
                    <Box className="create-schema-controls">
                        <Button variant="contained" onClick={handleSubmit}> Create Schema </Button>
                        { info && <Typography variant="h6" color={infoColor} className="flex-row-gap align-center padding"> <InfoIcon /> {info} </Typography> }
                    </Box>
                </Box>
                <Box className="create-schema-editor">
                    <Editor 
                        height="100%"
                        width="100%"
                        language={"json"}
                        defaultValue={jsonSchema}
                        onChange={(e) => setJsonSchema(e)}/>
                </Box>
            </Paper>
        </Box>
    );
}
 
export default CreateSchema;