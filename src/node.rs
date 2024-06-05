use std::fmt;

#[derive(Debug, PartialEq)]
pub enum Node {
    Bracket { expr: Box<Node> },
    Plus { lhs: Box<Node>, rhs: Box<Node> },
    Minus { lhs: Box<Node>, rhs: Box<Node> },
    Multiply { lhs: Box<Node>, rhs: Box<Node> },
    Divide { lhs: Box<Node>, rhs: Box<Node> },
    Power { lhs: Box<Node>, rhs: Box<Node> },
    Constant { value: i64 },
    Variable { name: String },
    Function { name: String, expr: Box<Node> },
}

impl fmt::Display for Node {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        match self {
            Node::Bracket { expr } => write!(f, "({})", expr),
            Node::Plus { lhs, rhs } => write!(f, "{}+{}", lhs, rhs),
            Node::Minus { lhs, rhs } => write!(f, "{}-{}", lhs, rhs),
            Node::Multiply { lhs, rhs } => write!(f, "{}*{}", lhs, rhs),
            Node::Divide { lhs, rhs } => write!(f, "{}/{}", lhs, rhs),
            Node::Power { lhs, rhs } => write!(f, "{}^{}", lhs, rhs),
            Node::Constant { value } => write!(f, "{}", value),
            Node::Variable { name } => write!(f, "{}", name),
            Node::Function { name, expr } => write!(f, "{}({})", name, expr),
        }
    }
}
