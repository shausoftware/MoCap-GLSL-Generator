'use strict';

import React from 'react';
import { useEffect } from 'react';

const SaveProject = (props) => {

    const [downloadFilename, setDownloadFilename] = React.useState("");
    const [fileDownloadUrl, setFileDownloadUrl] = React.useState("");

    const [enableJointCulling, setEnableJointCulling] = React.useState(false);
    const [firstDataJoint, setFirstDataJoint] =  React.useState(0);
    const [lastDataJoint, setLastDataJoint] =  React.useState(0);
    const [firstDataJointError, setFirstDataJointError] =  React.useState(false);
    const [lastDataJointError, setLastDataJointError] =  React.useState(false);

    let toolsRef = React.useRef();
    let downloadRef = React.useRef();
    let firstDataJointHelpRef = React.useRef();
    let lastDataJointHelpRef = React.useRef();

    const closeToolbar = (e) => {
        setDownloadFilename("");
        setFileDownloadUrl("");
        props.openDialog('saveProjectDialog');
    }

    const resetState = () => {
        setEnableJointCulling(false);
        setFirstDataJoint(0);
        setLastDataJoint(props.scene.frames[0] ? props.scene.frames[0].joints.length : 0);
        setFirstDataJointError(false);
        setLastDataJointError(false);
    }

    const downloadProject = (e) => {
        let doDownload = true;
        let project = {apiVersion: props.apiVersion,
            playbackParameters: Object.assign({}, props.playbackParameters),
            offset: Object.assign({}, props.offset),
            scene: Object.assign({}, props.scene)}
        if (enableJointCulling) {
            if (firstDataJoint >= lastDataJoint) {
                doDownload = false;
                setFirstDataJointError(true);
            } else if (lastDataJoint < firstDataJoint || lastDataJoint > props.scene.frames[0].joints.length) {
                doDownload = false;
                setLastDataJointError(true);
            } else {
                project.scene.frames.map((frame) => {
                    frame.joints = frame.joints.slice(firstDataJoint, lastDataJoint);
                });
            }
        }
        if (doDownload) {
            let filename = props.filename.substring(0, props.filename.indexOf(".")) + ".mcd";
            setDownloadFilename(filename);
            project.scene.filename = filename;
            const blob = new Blob([JSON.stringify(project, null, 4)]);
            setFileDownloadUrl(URL.createObjectURL(blob));
        }
    }

    const handleEnableJointCulling = (e) => {
        setEnableJointCulling(!enableJointCulling);
    }

    const handleFirstDataJointChange = (e) => {
        setFirstDataJoint(e.target.value);
    }

    const handleLastDataJointChange = (e) => {
        setLastDataJoint(e.target.value);
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

        if (firstDataJointError) {
            firstDataJointHelpRef.current.className = "form-text text-danger";
        } else {
            firstDataJointHelpRef.current.className = "form-text";
        }

        if (lastDataJointError) {
            lastDataJointHelpRef.current.className = "form-text text-danger";
        } else {
            lastDataJointHelpRef.current.className = "form-text";
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
                    <div className="modal-header">
                        <h5 className="modal-title" id="saveProjectModalLabel">Save Project</h5>
                        <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close" onClick={closeToolbar}></button>
                    </div>
                    <div className="modal-body">

                        <p>Save the current state of the project.</p>

                        <div className="mb-3">
                            <div className="row">
                                <div className="col-1">
                                    <input type="checkbox" className="form-check-input" id="enableLowRes" checked={enableJointCulling} onChange={handleEnableJointCulling}/>
                                </div>
                                <div className="col">
                                    <label htmlFor="enableLowRes" className="form-label">Enable Joint Culling</label>
                                </div>
                            </div>
                        </div>
                        <div className="mb-3">
                            <label htmlFor="firstDataJoint" className="form-label">First Data Joint</label>
                            <input type="number" min="0" className="form-control" id="firstDataJoint" aria-describedby="firstDataJointHelp" value={firstDataJoint} onChange={handleFirstDataJointChange} disabled={!enableJointCulling}/>
                            <div id="lowResStartHelp" ref={firstDataJointHelpRef} className="form-text">
                                Expecting value between 0 and {lastDataJoint}
                            </div>
                        </div>
                        <div className="mb-3">
                            <label htmlFor="lastDataJoint" className="form-label">Last Data Joint</label>
                            <input type="number" min="0" className="form-control" id="lastDataJoint" aria-describedby="lastDataJointHelp" value={lastDataJoint} onChange={handleLastDataJointChange} disabled={!enableJointCulling}/>
                            <div id="lowResStartHelp" ref={lastDataJointHelpRef} className="form-text">
                                Expecting value between {firstDataJoint} and {props.scene.frames[0] ? props.scene.frames[0].joints.length : 0}
                            </div>
                        </div>

                    </div>
                    <div className="modal-footer">
                        <a className="invisible" download={downloadFilename} href={fileDownloadUrl} ref={downloadRef}>download data</a>
                        <button type="button" className="btn btn-secondary" onClick={downloadProject}>Download Project</button>
                    </div>
               </div>
            </div>
        </div>
    );
}

export default SaveProject;