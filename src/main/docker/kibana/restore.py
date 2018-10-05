#!/usr/bin/env python
import json
import logging
import os
import sys

import requests

if sys.version_info < (3,):
    # for HTTPStatus.OK only
    import httplib as HTTPStatus
else:
    from http import HTTPStatus



OBJECT_TYPES = ['index-pattern', 'config', 'search', 'visualization', 'dashboard']

def parse_args(args):
    """ Parse arguments given to this script"""
    import argparse
    parser = argparse.ArgumentParser(
        description=('Restores the kibana configuration.'))
    parser.add_argument('-v', '--verbose', dest='log_level', action='store_const',
                        const=logging.DEBUG, default=logging.INFO,
                        help='Use verbose logging')
    parser.add_argument('-C', '--configuration_path',
                        default='./default',
                        help=('Path of the configuration to be restored.'
                              'Should contain at least one folder named %s or %s' %
                              (','.join(OBJECT_TYPES[:-1]), OBJECT_TYPES[-1])
                             )
                       )
    parser.add_argument('-H', '--kibana-host', default='http://localhost:5601',
                        help='Kibana endpoint.')
    parser.add_argument('-f', '--force', action='store_const',
                        const=True, default=False,
                        help='Overwrite configuration if needed.')

    return parser.parse_args(args)

def get_logger(args):
    """Creates the logger based on the provided arguments"""
    logging.basicConfig()
    logger = logging.getLogger(__name__)
    logger.setLevel(args.log_level)
    return logger

def main():
    ''' Main script function'''
    args = parse_args(sys.argv[1:])
    logger = get_logger(args)
    base_config_path = args.configuration_path

    # order to ensure dependency order is ok
    for obj_type in OBJECT_TYPES:
        obj_dir = os.path.sep.join((base_config_path, obj_type))

        if not os.path.exists(obj_dir):
            logger.info('No %s to restore, skipping.', obj_type)
            continue

        for obj_filename in os.listdir(obj_dir):
            with open(os.path.sep.join((obj_dir, obj_filename))) as obj_file:
                payload = obj_file.read()

            obj = json.loads(payload)

            obj_id = obj['id']
            for key in ('id', 'version', 'type', 'updated_at'):
                del obj[key]

            logger.info('Restoring %s id:%s (overwrite:%s)', obj_type, obj_id, args.force)
            url = "%s/api/saved_objects/%s/%s" % (args.kibana_host.rstrip("/"), obj_type, obj_id)
            params = {'overwrite': True} if args.force else {}
            post_object_req = requests.post(url,
                                            headers={'content-type': 'application/json',
                                                     'kbn-xsrf': 'True'},
                                            params=params,
                                            data=json.dumps(obj))
            if post_object_req.status_code == HTTPStatus.OK:
                logger.info('%s id:%s restored.', obj_type, obj_id)
            else:
                logger.warning(('Something bad happend while restoring %s id:%s. '
                                ' Received status code: %s'),
                               obj_type, obj_id, post_object_req.status_code)
                logger.warning('Body: %s', post_object_req.content)

if __name__ == "__main__":
    main()
