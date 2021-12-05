'use strict';

import React from 'react';
import { useEffect } from 'react';

const OpenProject = (props) => {

    const [selectedFile, setSelectedFile] = React.useState(undefined);
    const [filePath, setFilePath] = React.useState('');
    const [openProjectError, setOpenProjectError] = React.useState(false);

    let toolsRef = React.useRef();
    let filePathHelpRef = React.useRef();

    const closeToolbar = (e) => {
        props.openDialog('openProjectDialog');
    }

    const resetState = () => {
        setSelectedFile(undefined);
        setFilePath('');
        setOpenProjectError(false);
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        if (filePath && filePath != '') {
            const reader = new FileReader();
            reader.addEventListener("load", e => {
                props.openProject(JSON.parse(e.target.result));
            });
            reader.readAsText(selectedFile);
        } else {
            setOpenProjectError(true);
        }
    }

    const handleFilePathChange = (e) => {
        setOpenProjectError(false);
        setFilePath(e.target.value);
        setSelectedFile(e.target.files[0]);
    }

    useEffect(() => {

        if (props.updateProps) {
            resetState();
            props.setUpdateProps(false);
        }

        if (openProjectError) {
            filePathHelpRef.current.className = "form-text text-danger";
        } else {
            filePathHelpRef.current.className = "form-text";
        }

        if (props.showDialogState.openProjectDialog) {
            toolsRef.current.className = 'modal fade show';
            toolsRef.current.style.display = "block";
        } else {
            toolsRef.current.className = 'modal fade';
            toolsRef.current.style.display = "none";
        }
    });

    return(
        <div ref={toolsRef} className="modal fade" id="openProjectModal" tabIndex="-1" aria-labelledby="openProjectModalLabel" aria-hidden="true">
            <div className="modal-dialog modal-dialog-centered">
               <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title" id="openProjectModalLabel">Open Project</h5>
                        <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close" onClick={closeToolbar}></button>
                    </div>
                    <div className="modal-body">
                        <p>Open a previously saved project.</p>
                        <form onSubmit={handleSubmit} encType="multipart/form-data">
                            <div className="mb-3">
                                <label htmlFor="filePath" className="form-label">MoCap Project File Path</label>
                                <input type="file" accept=".mcd" multiple={false} value={filePath} onChange={handleFilePathChange} className="form-control" id="filePath" aria-describedby="filePathHelp" />
                                <div ref={filePathHelpRef} id="filePathHelp" className="form-text">Path to project .mcd file.</div>
                            </div>
                            <button type="submit" className="btn btn-secondary">Open Project</button>
                        </form>
                    </div>
               </div>
            </div>
        </div>
    );
}

export default OpenProject;