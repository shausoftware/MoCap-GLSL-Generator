'use strict';

import React from 'react';
import { useEffect } from 'react';

const Offset = (props) => {

    const [offsetX, setOffsetX] = React.useState(props.offset.x);
    const [offsetY, setOffsetY] = React.useState(props.offset.y);
    const [offsetZ, setOffsetZ] = React.useState(props.offset.y);
    const [offsetXError, setOffsetXError] = React.useState(false);
    const [offsetYError, setOffsetYError] = React.useState(false);
    const [offsetZError, setOffsetZError] = React.useState(false);

    let toolsRef = React.useRef();
    let offsetXHelpRef = React.useRef();
    let offsetYHelpRef = React.useRef();
    let offsetZHelpRef = React.useRef();

    const closeToolbar = (e) => {
        props.openDialog('offsetDialog');
    }

    const resetState = () => {
        setOffsetX(props.offset.x);
        setOffsetY(props.offset.y);
        setOffsetZ(props.offset.z);
        setOffsetXError(false);
        setOffsetYError(false);
        setOffsetZError(false);
    }

    const handleOffsetFormSubmit = (e) => {
        e.preventDefault();
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
            props.setOffsetCoordinates(offsetX, offsetY, offsetZ);
        }
    }

    const handleClearOffsetsClick = (e) => {
        setOffsetX('');
        setOffsetY('');
        setOffsetZ('');
        props.setAsCenterJoint(undefined);
    }

    const clearErrors = () => {
        setOffsetXError(false);
        setOffsetYError(false);
        setOffsetZError(false);
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

    const loadJointId = () => {
        return props.offset.jointId ? props.offset.jointId : "Undefined";
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
            toolsRef.current.className = 'modal fade show';
            toolsRef.current.style.display = "block";
        } else {
            toolsRef.current.className = 'modal fade';
            toolsRef.current.style.display = "none";
        }
    });

    return(
        <div ref={toolsRef} className="modal fade" id="offsetModal" tabIndex="-1" aria-labelledby="offsetModalLabel" aria-hidden="true">
            <div className="modal-dialog modal-dialog-centered">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title" id="offsetModalLabel">Offsets</h5>
                        <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close" onClick={closeToolbar}></button>
                    </div>
                    <div className="modal-body">
                        <div className="mb-3">
                            <label className="form-label">Center Joint ID: {loadJointId()}</label>
                        </div>
                        <form onSubmit={handleOffsetFormSubmit}>
                            <fieldset>
                                <div className="mb-3">
                                    <label htmlFor="offsetX" className="form-label">Offset X</label>
                                    <input type="number" className="form-control" id="offsetX" aria-describedby="offsetXHelp" value={offsetX} onChange={handleOffsetXChange}/>
                                    <div id="offsetXHelp" ref={offsetXHelpRef} className="form-text">Expecting X, Y, Z offset values}</div>
                                </div>
                                <div className="mb-3">
                                    <label htmlFor="offsetY" className="form-label">Offset Y</label>
                                    <input type="number" className="form-control" id="offsetY" aria-describedby="offsetYHelp" value={offsetY} onChange={handleOffsetYChange}/>
                                    <div id="offsetYHelp" ref={offsetYHelpRef} className="form-text">Expecting X, Y, Z offset values}</div>
                                </div>
                                <div className="mb-3">
                                    <label htmlFor="offsetZ" className="form-label">Offset Z</label>
                                    <input type="number" className="form-control" id="offsetZ" aria-describedby="offsetZHelp" value={offsetZ} onChange={handleOffsetZChange}/>
                                    <div id="offsetZHelp" ref={offsetZHelpRef} className="form-text">Expecting X, Y, Z offset values}</div>
                                </div>
                            </fieldset>
                            <div className="mb-3">
                                <button type="submit" className="btn btn-secondary">Update Offset</button>
                            </div>
                        </form>
                        <div className="mb-3">
                            <button type="button" className="btn btn-secondary" onClick={handleClearOffsetsClick}>
                                Clear Offsets
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Offset;