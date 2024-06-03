#[derive(Debug)]
pub enum Node {
    BracketNode(Box<Node>),
}
