'use strict';

import React from 'react';
import { useEffect } from 'react';

const Import = (props) => {

    const allAxis = ["x", "y", "z"];

    const [assignX, setAssignX] = React.useState("x");
    const [assignY, setAssignY] = React.useState("z");
    const [assignZ, setAssignZ] = React.useState("y");

    const [selectedFile, setSelectedFile] = React.useState(null);
    const [filePath, setFilePath] = React.useState('');
    const [errorMessage, setErrorMessage] = React.useState(undefined);

    let toolsRef = React.useRef();

    const closeToolbar = (e) => {
        props.openDialog('importDialog');
    }

    const resetState = () => {
        setSelectedFile(undefined);
        setFilePath('');
        setErrorMessage(undefined);
    }

    const importScene = async (data) => {
        const response = await fetch('/import', {
            method: 'POST',
            body: data
        });
        if (response.status !== 200) {
            throw new Error('Import Error: ' + response.status);
        }
        return await response.json();
    }

	const handleFilePathChange = (e) =>  {
	    setFilePath(e.target.value);
	    setSelectedFile(e.target.files[0]);
	    setErrorMessage(undefined);
	}

    const handleAxisClick = (e) => {
        if (e.target.id == "x") {
            setAssignX(e.target.text);
        } else if (e.target.id == "y") {
            setAssignY(e.target.text);
        } else if (e.target.id == "z") {
            setAssignZ(e.target.text);
        }
        setErrorMessage(undefined);
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        let valid = true;
        let test = [assignX, assignY, assignZ];
        if (!filePath && filePath === '') {
            valid = false;
            setErrorMessage('Please supply a file path for import');
        }
        if (!(test.includes("x") && test.includes("y") && test.includes("z"))) {
            valid = false;
            setErrorMessage('Expecting axis assignment with x, y and z values');
        }
        if (valid) {
            const data = new FormData();
            data.append('file', selectedFile);
            importScene(data).then(scene => {
                scene = updateAxis(scene);
                props.importScene(scene);
            //}).catch(e => console.log(e));
            }).catch(e => setErrorMessage("Server unable to import file"));
        }
    }

    const updateAxis = (scene) => {
        let axisUpdate = Object.assign({}, scene);
        axisUpdate.frames = axisUpdate.frames.map((frame) => {
            frame.joints = frame.joints.map((joint) => {
                let jointUpdate = Object.assign({}, joint);
                jointUpdate.x = joint[assignX];
                jointUpdate.y = joint[assignY];
                jointUpdate.z = joint[assignZ];
                return jointUpdate;
            });
            return frame;
        });
        return axisUpdate;
    }

    const axisOptions = (id, assignedAxis) => {
        return allAxis.map((axis) => {
            return(<li key={axis}>
                       <a className={assignedAxis == axis ? "dropdown-item active" : "dropdown-item"}
                       id={id}
                       onClick={handleAxisClick}
                       href="#">{axis}</a>
                   </li>);
        });
    }

    const loadError = () => {
        return errorMessage ? <div className="text-danger">{errorMessage}</div> : undefined;
    }

    useEffect(() => {

        if (props.updateProps) {
            resetState();
            props.setUpdateProps(false);
        }

        if (props.showDialogState.importDialog) {
            toolsRef.current.className = 'modal fade show';
            toolsRef.current.style.display = "block";
        } else {
            toolsRef.current.className = 'modal fade';
            toolsRef.current.style.display = "none";
        }
    });

    return(
        <div ref={toolsRef} className="modal fade" id="importModal" tabIndex="-1" aria-labelledby="importModalLabel" aria-hidden="true">
            <div className="modal-dialog modal-dialog-centered">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title" id="importModalLabel">Import MoCap Data</h5>
                        <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close" onClick={closeToolbar}></button>
                    </div>
                    <div className="modal-body">
                        <p>Import a C3D or TRC motion capture file and start a new project. Note: by default the X,Y,Z axis are transformed to X,Z,Y.</p>
                        {loadError()}
                        <form onSubmit={handleSubmit} encType="multipart/form-data">
                            <div className="mb-3">
                                <label htmlFor="filePath" className="form-label">Data File Path</label>
                                <input type="file" accept=".trc,.c3d" multiple={false} onChange={handleFilePathChange} className="form-control" id="filePath" aria-describedby="filePathHelp" />
                                <div id="filePathHelp" className="form-text">Path to C3D or TRC data file.</div>
                            </div>
                            <div className="mb-3">
                                <div className="row">
                                    <div className="col">
                                        <label htmlFor="assignX" className="form-label">Assign X</label>
                                        <div className="dropdown">
                                            <button className="btn btn-secondary btn-sm dropdown-toggle" type="button" id="assignX" data-bs-toggle="dropdown" aria-expanded="false">
                                                X Axis
                                            </button>
                                            <ul className="dropdown-menu" aria-labelledby="assignX">
                                                {axisOptions("x", assignX)}
                                            </ul>
                                        </div>
                                    </div>
                                    <div className="col">
                                        <label htmlFor="assignY" className="form-label">Assign Y</label>
                                        <div className="dropdown">
                                            <button className="btn btn-secondary btn-sm dropdown-toggle" type="button" id="assignX" data-bs-toggle="dropdown" aria-expanded="false">
                                                Y Axis
                                            </button>
                                            <ul className="dropdown-menu" aria-labelledby="assignY">
                                                {axisOptions("y", assignY)}
                                            </ul>
                                        </div>
                                    </div>
                                    <div className="col">
                                        <label htmlFor="assignZ" className="form-label">Assign Z</label>
                                        <div className="dropdown">
                                            <button className="btn btn-secondary btn-sm dropdown-toggle" type="button" id="assignZ" data-bs-toggle="dropdown" aria-expanded="false">
                                                Z Axis
                                            </button>
                                            <ul className="dropdown-menu" aria-labelledby="assignZ">
                                                {axisOptions("z", assignZ)}
                                            </ul>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div className="mb-3">
                                <button type="submit" className="btn btn-secondary">Import</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Import;