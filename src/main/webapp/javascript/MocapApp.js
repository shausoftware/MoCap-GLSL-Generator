'use strict';

import React from 'react';
import ReactDOM from 'react-dom';

import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';

import MocapPlayer from './viewer/MocapPlayer';

const MoCapApp = (props) => {

    return(
        <div className="container-fluid vh-100 p-0">
            <MocapPlayer />
        </div>
    );
}

export default MoCapApp;

ReactDOM.render(
	<MoCapApp />,
	document.getElementById('react-mountpoint')
)
