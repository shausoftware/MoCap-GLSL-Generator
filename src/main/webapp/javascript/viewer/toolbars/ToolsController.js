'use strict';

import React from 'react';

const ToolsController = (props) => {

    const handleViewParametersClick = (e) => {
        props.openDialog('viewDialog');
    }

    const handleOffsetDataClick = (e) => {
        props.openDialog('offsetDialog');
    }

    const handleJointDataClick = (e) =>  {
        props.openJointDialog();
    }

    const handleOpenProjectClick = (e) => {
        props.openDialog('openProjectDialog');
    }

    const handleSaveProjectClick = (e) => {
        props.openDialog('saveProjectDialog');
    }

    const handleImportClick = (e) => {
        props.openDialog('importDialog');
    }

    const handleAnalyseClick = (e) => {
        props.openDialog('analyseDialog');
    }

    const handleFourierClick = (e) => {
        props.openDialog('fourierDialog');
    }

    const handleShowStats = (e) => {
        props.openStats();
    }

    const handleShowHelp = (e) => {
        props.openDialog('helpDialog');
    }

    const loadMenuClass = () => {
        return props.sceneLoaded ? "dropdown-item" : "dropdown-item disabled";
    }

    return (
        <div className="container-fluid p-0">
            <div className="btn-toolbar justify-content-center border border-dark">
                <div className="btn-group" role="group">
                    <div className="dropdown">
                        <button className="btn btn-secondary btn-sm dropdown-toggle" type="button" id="tools" data-bs-toggle="dropdown" aria-expanded="false" disabled={!props.sceneLoaded}>
                            Tools
                        </button>
                        <ul className="dropdown-menu" aria-labelledby="tools">
                            <li>
                                <a className="dropdown-item" onClick={handleViewParametersClick}>
                                    View Parameters
                                </a>
                            </li>
                            <li>
                                <a className="dropdown-item" onClick={handleOffsetDataClick}>
                                    Offsets
                                </a>
                            </li>
                            <li>
                                <a className="dropdown-item" onClick={handleJointDataClick}>
                                    Joint Data
                                </a>
                            </li>
                            <li>
                                <a className="dropdown-item" onClick={handleAnalyseClick}>
                                    Analyse
                                </a>
                            </li>
                            <li>
                                <a className="dropdown-item" onClick={handleFourierClick}>
                                    Fourier
                                </a>
                            </li>
                        </ul>
                    </div>
                    <div className="dropdown">
                        <button className="btn btn-secondary btn-sm dropdown-toggle" type="button" id="project" data-bs-toggle="dropdown" aria-expanded="false">
                            Project
                        </button>
                        <ul className="dropdown-menu" aria-labelledby="project">
                            <li>
                                <a className="dropdown-item" onClick={handleOpenProjectClick}>
                                    Open Project
                                </a>
                            </li>
                            <li>
                                <a className={loadMenuClass()} onClick={handleSaveProjectClick}>
                                    Save Project
                                </a>
                            </li>
                            <li>
                                <a className="dropdown-item" onClick={handleImportClick}>
                                    Import MoCap Data
                                </a>
                            </li>
                        </ul>
                    </div>
                    <button type="button" className="btn btn-secondary btn-sm" onClick={handleShowStats}>Stats</button>
                    <button type="button" className="btn btn-secondary btn-sm" onClick={handleShowHelp}>?</button>
                </div>
            </div>
        </div>
    );
}

export default ToolsController;