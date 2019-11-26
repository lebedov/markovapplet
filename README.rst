.. -*- rst -*-

MarkovApplet
============

Package Description
-------------------
MarkovApplet is a Java applet implementation of an M/M/s queue simulator.
One may specify the number of servers, the arrival rate, and the
service. M/M/infinity simulation is also supported.

Installation
------------
This applet makes use of JFreeChart [2]_, a freely available chart
construction class library for Java. The specific
version of the JFreeChart packages used in developing the applet are
included with the applet code to facilitate compilation and running.

To build the applet, adjust the CLASSPATH in the accompanying Makefile
and run::

   make jar

To run the applet, run:: 

   appletviewer MarkovApplet.java

from within the source directory.

Author
-------
See the included `AUTHORS.rst <https://github.com/lebedov/markovapplet/blob/master/AUTHORS.rst>`_ file
for more information.

License
-------
This software is licensed under the 
`BSD License <http://www.opensource.org/licenses/bsd-license.php>`_.
See the included `LICENSE.rst <https://github.com/lebedov/markovapplet/blob/master/LICENSE.rst>`_ 
file for more information.

JFreeChart is licensed under the `GNU Lesser General Public License (LGPL)
<http://www.gnu.org/licenses/lgpl.html>`_. See the included `LICENSE.jfreechart
<https://github.com/lebedov/markovapplet/blob/master/license.jfreechart>`_
file for more information.

.. [1] http://bionet.ee.columbia.edu/
.. [2] http://www.jfree.org/jfreechart/

