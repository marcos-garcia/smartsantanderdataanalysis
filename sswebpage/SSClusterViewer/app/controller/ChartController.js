/*
 * File: app/controller/ChartController.js
 *
 * This file was generated by Sencha Architect version 3.0.4.
 * http://www.sencha.com/products/architect/
 *
 * This file requires use of the Ext JS 4.2.x library, under independent license.
 * License of Sencha Architect does not include license for Ext JS 4.2.x. For more
 * details see http://www.sencha.com/license or contact license@sencha.com.
 *
 * This file will be auto-generated each and everytime you save your project.
 *
 * Do NOT hand edit this file.
 */

Ext.define('SSClusterViewer.controller.ChartController', {
    extend: 'Ext.app.Controller',

    loadDay: function(date) {
        window.seldate = date;
        var store = Ext.data.StoreManager.lookup('DayValuesStore');

        var d = new Date();


        console.log(date.toISOString());
        console.log(date.toString());

        date.setTime(date.getTime() + (1000 * 60 * 60 * 24));

        store.load({
        params:{
            'measure'  : 'temperature',
            'day': date.toISOString().substr(0,10)+"T00:00:00.000+02:00"
        }
        });
    },

    loadCluster: function(clusterid) {
        window.clusterid = clusterid;
        var store = Ext.data.StoreManager.lookup('ClusterCentersStore');

        store.load({
        params:{'clusterid': clusterid}
        });
    },

    updateSerie: function(id, name, data) {
        window.chart.series[id].update({
            name: name,
            data: data
        });
    }

});
