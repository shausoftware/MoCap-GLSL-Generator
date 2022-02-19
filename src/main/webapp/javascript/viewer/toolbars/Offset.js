'use strict';

import React from 'react';
import { useEffect } from 'react';

const Offset = (props) => {

    const JOINT_UNDEFINED = "Undefined"

    const [jointId, setJointId] = React.useState(undefined);
    const [offsetX, setOffsetX] = React.useState(props.offset.x);
    const [offsetY, setOffsetY] = React.useState(props.offset.y);
    const [offsetZ, setOffsetZ] = React.useState(props.offset.y);
    const [offsetXError, setOffsetXError] = React.useState(false);
    const [offsetYError, setOffsetYError] = React.useState(false);
    const [offsetZError, setOffsetZError] = React.useState(false);
    const [constrainX, setConstrainX] = React.useState(true);
    const [constrainY, setConstrainY] = React.useState(true);
    const [constrainZ, setConstrainZ] = React.useState(true);

    let toolsRef = React.useRef();
    let offsetXHelpRef = React.useRef();
    let offsetYHelpRef = React.useRef();
    let offsetZHelpRef = React.useRef();

    const closeToolbar = (e) => {
        props.openDialog('offsetDialog');
    }

    const resetState = () => {
        setJointId(props.offset.jointId ? props.offset.jointId : undefined);
        setOffsetX(props.offset.x);
        setOffsetY(props.offset.y);
        setOffsetZ(props.offset.z);
        setOffsetXError(false);
        setOffsetYError(false);
        setOffsetZError(false);
        setConstrainX(props.offset.constrainX);
        setConstrainY(props.offset.constrainY);
        setConstrainZ(props.offset.constrainZ);
    }

    const handleOffsetFormSubmit = (e) => {
        e.preventDefault();
        if (jointId === JOINT_UNDEFINED) {
            let valid = true;
            if (!offsetX || offsetX == '') {
                valid = false;
                setOffsetXError(true);
            } else if (!offsetY || offsetY == '') {
                valid = false;
                setOffsetYError(true);
            } else if (!offsetZ || offsetZ == '') {
                valid = false;
                setOffsetZError(true);
            }
            if (valid) {
                props.setOffsetCoordinates(offsetX, offsetY, offsetZ, constrainX, constrainY, constrainZ);
            }
        } else {
            props.setAsCenterJoint(jointId, constrainX, constrainY, constrainZ);
        }
    }

    const handleClearOffsetsClick = (e) => {
        setOffsetX('');
        setOffsetY('');
        setOffsetZ('');
        setJointId(undefined);
        setConstrainX(true);
        setConstrainY(true);
        setConstrainZ(true);
        props.setAsCenterJoint(undefined, true, true, true);
        clearErrors();
    }

    const clearErrors = () => {
        setOffsetXError(false);
        setOffsetYError(false);
        setOffsetZError(false);
    }

    const handleJointIdClick = (e) => {
        setJointId(e.target.text === JOINT_UNDEFINED ? undefined : e.target.text);
        clearErrors();
    }

    const handleOffsetXChange = (e) => {
        setOffsetX(e.target.value);
        clearErrors();
    }

    const handleOffsetYChange = (e) => {
        setOffsetY(e.target.value);
        clearErrors();
    }

    const handleOffsetZChange = (e) => {
        setOffsetZ(e.target.value);
        clearErrors();
    }

    const handleConstrainXClick = (e) => {
        setConstrainX(!constrainX);
    }

    const handleConstrainYClick = (e) => {
        setConstrainY(!constrainY);
    }

    const handleConstrainZClick = (e) => {
        setConstrainZ(!constrainZ);
    }

    const jointIdOptions = () => {
        let options = [];
        options.push(<li key={-1}>
                <a className={jointId ? "dropdown-item" : "dropdown-item active"}
                   onClick={handleJointIdClick}
                   href="#">{JOINT_UNDEFINED}</a>
            </li> );
        if (props.frames.length > 0) {
            props.frames[0].joints.map((joint) => {
                options.push(<li key={joint.id}>
                    <a className={joint.id == jointId ? "dropdown-item active" : "dropdown-item"}
                       onClick={handleJointIdClick}
                       href="#">{joint.id}</a>
                </li>);
            });
        }
        return options;
    }

    useEffect(() => {

        if (props.updateProps) {
            resetState();
            props.setUpdateProps(false);
        }

        if (offsetXError) {
            offsetXHelpRef.current.className = "form-text text-danger";
        } else {
            offsetXHelpRef.current.className = "form-text";
        }
        if (offsetYError) {
            offsetYHelpRef.current.className = "form-text text-danger";
        } else {
            offsetYHelpRef.current.className = "form-text";
        }
        if (offsetZError) {
            offsetZHelpRef.current.className = "form-text text-danger";
        } else {
            offsetZHelpRef.current.className = "form-text";
        }

        if (props.showDialogState.offsetDialog)  {
            toolsRef.current.className = 'offcanvas offcanvas-start show';
            toolsRef.current.style.visibility = "visible";
        } else {
            toolsRef.current.className = 'offcanvas offcanvas-start';
            toolsRef.current.style.visibility = "";
        }
    });

    return(
        <div ref={toolsRef} className="offcanvas offcanvas-start" tabIndex="-1" id="offset" aria-labelledby="offsetLabel">
            <div className="offcanvas-header bg-dark text-white">
                <h5 className="offcanvas-title" id="offsetModalLabel">Offsets</h5>
                <button type="button" className="btn-close bg-light" data-bs-dismiss="modal" aria-label="Close" onClick={closeToolbar}></button>
            </div>
            <div className="offcanvas-body bg-dark text-white">
                <form onSubmit={handleOffsetFormSubmit}>
                    <fieldset>
                        <div className="mb-3">
                            <div className="dropdown">
                                <button className="btn btn-secondary btn-sm dropdown-toggle" type="button" id="jointId" data-bs-toggle="dropdown" aria-expanded="false">
                                    Centre Joint ID
                                </button>
                                <ul className="dropdown-menu scrollable-menu" aria-labelledby="assignX">
                                    {jointIdOptions()}
                                </ul>
                            </div>
                        </div>
                        <div className="mb-3">
                            <label htmlFor="offsetX" className="form-label">Offset X</label>
                            <input type="number" className="form-control" id="offsetX" aria-describedby="offsetXHelp" value={offsetX} onChange={handleOffsetXChange} disabled={jointId}/>
                            <div id="offsetXHelp" ref={offsetXHelpRef} className="form-text">Expecting X, Y, Z offset values}</div>
                        </div>
                        <div className="mb-3">
                            <label htmlFor="offsetY" className="form-label">Offset Y</label>
                            <input type="number" className="form-control" id="offsetY" aria-describedby="offsetYHelp" value={offsetY} onChange={handleOffsetYChange} disabled={jointId}/>
                            <div id="offsetYHelp" ref={offsetYHelpRef} className="form-text">Expecting X, Y, Z offset values}</div>
                        </div>
                        <div className="mb-3">
                            <label htmlFor="offsetZ" className="form-label">Offset Z</label>
                            <input type="number" className="form-control" id="offsetZ" aria-describedby="offsetZHelp" value={offsetZ} onChange={handleOffsetZChange} disabled={jointId}/>
                            <div id="offsetZHelp" ref={offsetZHelpRef} className="form-text">Expecting X, Y, Z offset values}</div>
                        </div>
                    </fieldset>
                    <div className="mb-3 text-center">
                        <div className="btn-group" role="group" aria-label="Constraints">
                            <button type="button" className={constrainX ? "btn btn-success" : "btn btn-dark"} onClick={handleConstrainXClick}>Constrain X</button>
                            <button type="button" className={constrainY ? "btn btn-success" : "btn btn-dark"} onClick={handleConstrainYClick}>Constrain Y</button>
                            <button type="button" className={constrainZ ? "btn btn-success" : "btn btn-dark"} onClick={handleConstrainZClick}>Constrain Z</button>
                        </div>
                    </div>
                    <div className="text-center">
                        <button type="submit" className="btn btn-secondary">Update Offset</button>
                        <button type="button" className="btn btn-secondary" onClick={handleClearOffsetsClick}>Clear Offsets</button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default Offset;