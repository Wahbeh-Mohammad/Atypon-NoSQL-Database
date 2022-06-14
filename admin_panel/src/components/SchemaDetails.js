import React, { useState, useEffect } from 'react';
import { Box, Typography, Button, List, ListItemButton, Divider, Paper,
         Table, TableContainer, TableHead, TableRow, TableCell, TableBody,
         TextField, Select, MenuItem, FormControl, InputLabel } from "@mui/material";
import CreateNewDocument from "./CreateNewDocument";
import Indexes from "./Indexes";
import Document from "./Document";
import Editor from '@monaco-editor/react';
import Cookies from 'universal-cookie';

const SchemaDetails = (props) => {
    const { databaseName, schema, schemaName } = props;
    
    const cookie = new Cookies();
    // views
    const [view, setView] = useState("details");

    // updating the schema
    const [jsonSchemaUpdate, setJsonSchemaUpdate] = useState("{}");
    const [updateInfo, setUpdateInfo] = useState("");
    const [updateInfoColor, setUpdateInfoColor] = useState("");

    const handleUpdateSchema = () => {
        const token = cookie.get("token");
        fetch(
            `${process.env.REACT_APP_ADMINCONTROLLER_URL}/update/${databaseName}/schema?schemaName=${schemaName}`,
            {
                method:"PUT",
                headers: {
                    "content-type": "application/json",
                    "authorization": token
                },
                body: jsonSchemaUpdate
            })
        .then(response => response.json())
        .then(data => {
            if(data.status === "GOOD") {
                setUpdateInfoColor("green")
                setUpdateInfo(data.message);
                setTimeout(()=>{
                    window.location.reload();
                }, 800)
            } else {
                setUpdateInfoColor("red");
                setUpdateInfo(data.message);
            }
        })
    }

    // deleting the schema
    const [deleteInfo, setDeleteInfo] = useState("");
    const [deleteInfoColor, setDeleteInfoColor] = useState("");
    const [deleteDocsInfo, setDeleteDocsInfo] = useState("");
    const [deleteDocsInfoColor, setDeleteDocsInfoColor] = useState("");

    const handleDeleteAllDocuments = () => {
        const token = cookie.get("token");
        fetch(`${process.env.REACT_APP_ADMINCONTROLLER_URL}/delete/${databaseName}/${schemaName}/document/all`, {
            method:"DELETE",
            headers: {
                "authorization": token
            }
        })
        .then(response => response.json())
        .then(data => {
            if(data.status === "GOOD") {
                setAllDocuments([]);
                setFilteredDocuments([]);
                setDeleteDocsInfo(data.message);
                setDeleteDocsInfoColor("green");
            } else {
                setDeleteDocsInfo(data.message);
                setDeleteDocsInfoColor("red");
            }
        })

    }

    const handleDeleteSchema = () => {
        const token = cookie.get("token");
        fetch(
            `${process.env.REACT_APP_ADMINCONTROLLER_URL}/delete/${databaseName}/schema?schemaName=${schemaName}`,{
            method:"DELETE",
            headers: {
                "authorization": token
            }
        })
        .then(response => response.json())
        .then(data => {
            if(data.status === "GOOD") {
                setDeleteInfo(data.message);
                setDeleteInfoColor("green");
                setTimeout(()=>{
                    window.location.reload();
                }, 800)
            } else {
                setDeleteInfo(data.message);
                setDeleteInfoColor("red");
            }
        });
    }

    // documents
    const [allDocuments, setAllDocuments] = useState([]);
    const [filteredDocuments, setFilteredDocuments] = useState([]);
    const [documentsView, setDocumentsView] = useState("default");
    
    // filter
    const [fieldName, setFieldName] = useState("");
    const [operation, setOperation] = useState("equals");
    const [compareTo, setCompareTo] = useState("");

    const handleFilter = () => {
        setFilteredDocuments([]);
        setDocumentsView("filtered")
        const token = cookie.get("read-token");
        fetch(
        `${process.env.REACT_APP_READCONTROLLER_URL}/read/${databaseName}/${schemaName}/filter?fieldName=${fieldName}&op=${operation}&compareTo=${compareTo}`, {
            method:"GET",
            headers: {
                "authorization":token
            }
        })
        .then(response => response.json())
        .then(data => {
            console.log(data);
            if(data.status === "GOOD") {
                setFilteredDocuments(data.content);
            }
        });        
    }
    
    const handleResetFilter = () => {
        setFilteredDocuments([]);
        setDocumentsView("default")
        setFieldName("");
        setCompareTo("");
    }

    const fetchDocuments = () => {
        if(!databaseName || !schemaName)
            return;
        const token = cookie.get("read-token");
        fetch(`${process.env.REACT_APP_READCONTROLLER_URL}/read/${databaseName}/${schemaName}/document/all`, {
            method:"GET",
            headers: {
                "authorization": token
            }
        })
        .then(response => response.json())
        .then(data => {
            if(data.status === "GOOD") {
                setAllDocuments(data.content)
            } else {
                console.log(data.message);
            }
        });
    }

    useEffect(()=> {
        if(view === "documents") {
            fetchDocuments();
        }
    }, [view]);

    useEffect(() => {
        fetchDocuments();
        setView("details")
    },[schemaName])

    return (
        <Box className="schema-details">
            <Box className="schema-interaction-controls" sx={{borderRight:1, borderColor:'divider'}}>
                <Typography className="connected-to" color="primary" variant="h4" sx={{borderBottom:1, borderColor:"divider"}}> 
                    Connected To {schemaName} 
                </Typography>
                <List>
                    <ListItemButton onClick={e => setView("details")}>
                        <Typography color="primary" variant="h6"> Schema details </Typography>
                    </ListItemButton>
                    <Divider />
                    <ListItemButton onClick={e => setView("documents")}>
                        <Typography color="primary" variant="h6"> Documents </Typography>
                    </ListItemButton>
                    <Divider />
                    <ListItemButton onClick={e => setView("create")}>
                        <Typography color="primary" variant="h6"> Create new document </Typography>
                    </ListItemButton>
                    <Divider />
                    <ListItemButton onClick={e => setView("indexes")}>
                        <Typography color="primary" variant="h6"> Indexes </Typography>
                    </ListItemButton>
                </List>
            </Box>
            <Box className="schema-view-details">
                { view === "details" && 
                    <Box className="section part flex-col-gap">
                        <Paper elevation={6} className="flex-col-gap part">
                            <Typography variant="h4" color="primary"> Schema's Fields </Typography>
                            <Box className="small-width">
                                <TableContainer component={Paper}>
                                    <Table>
                                        <TableHead>
                                            <TableRow>
                                                <TableCell align="center">
                                                    Field name
                                                </TableCell>
                                                <TableCell align="center">
                                                    Field Type
                                                </TableCell>
                                            </TableRow>
                                        </TableHead>
                                        <TableBody>
                                            {
                                                schema && Object.keys(schema).map((fieldName, idx) => {
                                                    return (
                                                        <TableRow key={idx}>
                                                            <TableCell align="center">
                                                                {fieldName}
                                                            </TableCell>
                                                            <TableCell align="center">
                                                                {schema[fieldName]}
                                                            </TableCell>
                                                        </TableRow>
                                                    )
                                                })
                                            }
                                        </TableBody>
                                    </Table>
                                </TableContainer>
                            </Box>
                        </Paper>
                        <Paper elevation={6} className="flex-col-gap part">
                            <Typography color="darkorange" variant="h4"> Update schema </Typography>
                            <Typography color="darkorange" variant="h6"> 
                                Updates that happen on a schema are not inflicted on documents. 
                            </Typography>
                            <Box>
                                <Editor 
                                    className="min-height"
                                    height="25rem"
                                    width="100%"
                                    language={"json"}
                                    value={jsonSchemaUpdate}
                                    onChange={(e) => setJsonSchemaUpdate(e)} />
                            </Box>
                            <div className="flex-row-gap align-center">
                                <Button color="warning" variant="contained" onClick={handleUpdateSchema}> Update schema </Button>
                                { updateInfo && <Typography variant="h6" color={updateInfoColor}> {updateInfo} </Typography> }
                            </div>
                        </Paper>
                        <Paper elevation={6} className="flex-col-gap part">
                            <Typography color="error" variant="h4"> Danger Zone </Typography>
                            <Typography color="error" variant="h6">
                                Delete all documents
                            </Typography>
                            <div className="flex-row-gap align-center">
                                <Button color="error" variant="contained" onClick={handleDeleteAllDocuments}> Delete all documents </Button>
                                { deleteDocsInfo && <Typography variant="h6" color={deleteDocsInfoColor}> {deleteDocsInfo} </Typography> }                                
                            </div>
                            <Typography color="error" variant="h6"> 
                                Deleting the schema will also delete all its documents.
                            </Typography>
                            <div className="flex-row-gap align-center">
                                <Button color="error" variant="contained" onClick={handleDeleteSchema}> Delete Schema </Button>
                                { deleteInfo && <Typography variant="h6" color={deleteInfoColor}> {deleteInfo} </Typography> }                                
                            </div>
                        </Paper>
                    </Box>
                }
                { view === "documents" && 
                    <Box className="section flex-col-gap">
                        <Paper className="flex-row-gap padding align-center">
                            <Typography color="primary" variant="h6">Filter</Typography>
                            <Box className="padding filter-inputs flex-row-gap">
                                <TextField 
                                    autoComplete="off" 
                                    size="small" 
                                    variant="outlined" 
                                    label="Field Name" 
                                    value={fieldName}
                                    onChange={(e)=>{ setFieldName(e.target.value)}}/>
                                <FormControl sx={{ minWidth: 220 }} size="small">
                                    <InputLabel id="select-small">Operation</InputLabel>
                                    <Select
                                        labelId="select-small"
                                        id="select-small"
                                        label="Operation"
                                        value={operation}
                                        onChange={e => setOperation(e.target.value)}
                                        color="primary">
                                        <MenuItem value={"equals"}> == </MenuItem>
                                        <MenuItem value={"notEquals"}> != </MenuItem>
                                    </Select>
                                </FormControl>
                                <TextField 
                                    autoComplete="off" 
                                    size="small" 
                                    variant="outlined" 
                                    label="Compare To" 
                                    value={compareTo}
                                    onChange={(e)=>{ setCompareTo(e.target.value)}}/>
                            </Box>
                            <Box className="flex-row-gap">
                                <Button variant="contained" color="success" onClick={handleFilter}> Filter </Button>
                                <Button variant="contained" color="success" onClick={handleResetFilter}> Refresh </Button>
                            </Box>
                        </Paper>
                        <Box className="all-documents flex-col-gap">
                            { documentsView === "default" && allDocuments &&
                                allDocuments.map((document, idx) => {
                                    return (
                                        <Document key={idx} 
                                            databaseName={databaseName}
                                            schemaName={schemaName}
                                            fullDocument={document} />
                                    )
                                })
                            }
                            { documentsView === "filtered" && filteredDocuments &&
                                filteredDocuments.map((document, idx) => {
                                    return (
                                        <Document key={idx} 
                                            databaseName={databaseName}
                                            schemaName={schemaName}
                                            fullDocument={document} />
                                    )
                                })
                            }
                        </Box>
                    </Box>
                }
                { view === "create" && 
                    <Box className="padding">
                        <CreateNewDocument schemaName={schemaName} databaseName={databaseName}/>
                    </Box>
                }
                { view === "indexes" && 
                    <Box className="padding"> 
                        <Indexes schemaName={schemaName} databaseName={databaseName}/>
                    </Box>
                }
            </Box>
        </Box> 
    );
}
 
export default SchemaDetails;