#!/usr/bin/env python
import json
import logging
import os
import sys

import requests

PER_PAGE = 1000

def parse_args(args):
    """ Parse arguments given to this script""" 
    import argparse
    parser = argparse.ArgumentParser(
        description=('Description of the script'))
    parser.add_argument('-v', '--verbose', dest='log_level', action='store_const',
                        const=logging.DEBUG, default=logging.INFO,
                        help='Use verbose logging')
    parser.add_argument('-C', '--configuration_path',
                        default='./default',
                        help='Path of the configuration to be backed up.')
    parser.add_argument('-f', '--force', action='store_const',
                        const=True, default=False,
                        help=('If the save folder already exists, overwrite files'
                              ' matching a configuration item that should be written.'
                              ' Files already in the folder that do not match are'
                              ' left as-is.'))
    parser.add_argument('-H', '--kibana-host', default='http://localhost:5601',
                        help='Kibana endpoint.')

    return parser.parse_args(args)

def get_logger(args):
    """Creates the logger based on the provided arguments"""
    logging.basicConfig()
    logger = logging.getLogger(__name__)
    logger.setLevel(args.log_level)
    return logger

def main():
    """ This script dumps the kibana configuration from Kibana"""
    args = parse_args(sys.argv[1:])

    base_config_path = args.configuration_path

    # get list of all objects available
    url = "%s/api/saved_objects/" % (args.kibana_host.rstrip("/"),)
    saved_objects_req = requests.get(url,
                                     params={'per_page': PER_PAGE})

    saved_objects = saved_objects_req.json()['saved_objects']

    for obj in saved_objects:

        obj_folder = os.path.sep.join((base_config_path, obj['type']))

        if not os.path.exists(obj_folder):
            os.makedirs(obj_folder)

        filename = "%s/%s-%s.json" % (obj_folder, obj['type'], obj['id'])
        with open(filename, 'w') as file:
            json.dump(obj, fp=file)


if __name__ == "__main__":
    main()
