'use strict';

import React from 'react';
import { useEffect } from 'react';

const SaveProject = (props) => {

    const [downloadFilename, setDownloadFilename] = React.useState("");
    const [fileDownloadUrl, setFileDownloadUrl] = React.useState("");

    let toolsRef = React.useRef();
    let downloadRef = React.useRef();

    const closeToolbar = (e) => {
        setDownloadFilename("");
        setFileDownloadUrl("");
        props.openDialog('saveProjectDialog');
    }

    const downloadProject = (e) => {
        let filename = props.filename.substring(0, props.filename.indexOf(".")) + ".mcd";
        setDownloadFilename(filename);
        props.project.scene.filename = filename;
        const blob = new Blob([JSON.stringify(props.project, null, 4)]);
        setFileDownloadUrl(URL.createObjectURL(blob));
    }

    useEffect(() => {

        if (fileDownloadUrl != "") {
            downloadRef.current.click();
            URL.revokeObjectURL(fileDownloadUrl);
            closeToolbar();
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
                        Save the current state of the project.
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