import type {ViewProps} from 'ViewPropTypes';
import type {HostComponent} from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';
import {DirectEventHandler} from 'react-native/Libraries/Types/CodegenTypes';

type Event = ReadOnly<{
  value: string;
}>;

export interface NativeProps extends ViewProps {
  onCameraStart?: DirectEventHandler<Event>;
}

export default codegenNativeComponent<NativeProps>(
  'RTNFaceLandmarker',
) as HostComponent<NativeProps>;