'use strict';

import React from 'react';
import { useEffect } from 'react';

const SaveProject = (props) => {

    const [filename, setFilename] = React.useState("");
    const [fileDownloadUrl, setFileDownloadUrl] = React.useState("");
    const [filenameError, setFilenameError] = React.useState(false);
    const [deleteHiddenJoints, setDeleteHiddenJoints] = React.useState(false);

    let toolsRef = React.useRef();
    let downloadRef = React.useRef();
    let filenameHelpRef = React.useRef();

    const closeToolbar = (e) => {
        setFileDownloadUrl("");
        props.openDialog('saveProjectDialog');
    }

    const resetState = () => {
        setFilename(props.filename.substring(0, props.filename.indexOf(".")));
        setDeleteHiddenJoints(false);
        setFilenameError(false);
    }

    const downloadProject = (e) => {
        if (filename) {
            let project = {apiVersion: props.apiVersion,
                playbackParameters: Object.assign({}, props.playbackParameters),
                offset: Object.assign({}, props.offset),
                scene: Object.assign({}, props.scene)};
            if (deleteHiddenJoints) {
                project.scene.frames = project.scene.frames.map((frame) => {
                    frame.joints = frame.joints.filter(joint => joint.display);
                    return frame;
                })
            }
            project.scene.filename = filename + ".mcd";
            const blob = new Blob([JSON.stringify(project, null, 4)]);
            setFileDownloadUrl(URL.createObjectURL(blob));
        } else {
            setFilenameError(true);
        }
    }

    const handleFilenameChange = (e) => {
        setFilename(e.target.value);
    }

    const handleDeleteHiddenJointsChange = (e) => {
        setDeleteHiddenJoints(e.target.value);
    }

    useEffect(() => {

        if (props.updateProps) {
            resetState();
            props.setUpdateProps(false);
        }

        if (fileDownloadUrl != "") {
            downloadRef.current.click();
            URL.revokeObjectURL(fileDownloadUrl);
            closeToolbar();
        }

        if (filenameError) {
            filenameHelpRef.current.className = "form-text text-danger";
        } else {
            filenameHelpRef.current.className = "form-text";
        }

        if (props.showDialogState.saveProjectDialog) {
            toolsRef.current.className = 'modal fade show';
            toolsRef.current.style.display = "block";
        } else {
            toolsRef.current.className = 'modal fade';
            toolsRef.current.style.display = "none";
        }
    });

    return(
        <div ref={toolsRef} className="modal fade" id="saveProjectModal" tabIndex="-1" aria-labelledby="saveProjectModalLabel" aria-hidden="true">
            <div className="modal-dialog modal-dialog-centered">
               <div className="modal-content">
                    <div className="modal-header bg-dark text-white">
                        <h5 className="modal-title" id="saveProjectModalLabel">Save Project</h5>
                        <button type="button" className="btn-close bg-light" data-bs-dismiss="modal" aria-label="Close" onClick={closeToolbar}></button>
                    </div>
                    <div className="modal-body bg-dark text-white">

                        <p>Save the current state of the project.</p>

                        <div className="mb-3">
                            <label htmlFor="filename" className="form-label">Filename</label>
                            <input min="0" className="form-control" id="filename" aria-describedby="filenameHelp" value={filename} onChange={handleFilenameChange}/>
                            <div id="filenameHelp" ref={filenameHelpRef} className="form-text">
                                Filename required
                            </div>
                        </div>
                        <div className="mb-3">
                            <div className="row">
                                <div className="col-1">
                                    <input type="checkbox" className="form-check-input" id="deleteHiddenJoints" checked={deleteHiddenJoints} onChange={handleDeleteHiddenJointsChange}/>
                                </div>
                                <div className="col">
                                    <label htmlFor="deleteHiddenJoints" className="form-label">Delete Hidden Joints</label>
                                </div>
                            </div>
                        </div>

                    </div>
                    <div className="modal-footer bg-dark text-white">
                        <a className="invisible" download={filename + ".mcd"} href={fileDownloadUrl} ref={downloadRef}>download data</a>
                        <button type="button" className="btn btn-secondary" onClick={downloadProject}>Download Project</button>
                    </div>
               </div>
            </div>
        </div>
    );
}

export default SaveProject;